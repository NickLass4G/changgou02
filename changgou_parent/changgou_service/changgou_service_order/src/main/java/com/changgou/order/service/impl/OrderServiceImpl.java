package com.changgou.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fescar.spring.annotation.GlobalTransactional;
import com.changgou.common.util.IdWorker;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.order.config.RabbitMQConfig;
import com.changgou.order.dao.OrderItemMapper;
import com.changgou.order.dao.OrderLogMapper;
import com.changgou.order.dao.OrderMapper;
import com.changgou.order.dao.TaskMapper;
import com.changgou.order.pojo.Order;
import com.changgou.order.pojo.OrderItem;
import com.changgou.order.pojo.OrderLog;
import com.changgou.order.pojo.Task;
import com.changgou.order.service.CartService;
import com.changgou.order.service.OrderService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private CartService cartService;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SkuFeign skuFeign;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private OrderLogMapper orderLogMapper;


    /**
     * 查询全部列表
     *
     * @return
     */
    @Override
    public List<Order> findAll() {
        return orderMapper.selectAll();
    }

    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    @Override
    public Order findById(String id) {
        return orderMapper.selectByPrimaryKey(id);
    }


    /**
     * 增加
     *
     * @param order
     */
    @Override
    @GlobalTransactional(name = "order_add")
    public String add(Order order) {
        // 获取购物车里所有的商品项
        Map cartMap = cartService.findList(order.getUsername());
        List<OrderItem> orderItemList = (List<OrderItem>) cartMap.get("orderItemList");

        // 统计计算:总数量,总金额
        order.setTotalNum((Integer) cartMap.get("totalNum"));
        order.setTotalMoney((Integer) cartMap.get("totalMoney"));
        // 填充订单数据并保存
        order.setPayMoney((Integer) cartMap.get("totalMoney"));
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());
        order.setBuyerRate("0");
        order.setSourceType("1");
        order.setOrderStatus("0");
        order.setPayStatus("0");
        order.setConsignStatus("0");
        String orderId = idWorker.nextId() + "";
        order.setId(orderId);
        orderMapper.insertSelective(order);

        // 获取每一个购物项保存到item
        for (OrderItem orderItem : orderItemList) {
            orderItem.setId(idWorker.nextId() + "");
            orderItem.setIsReturn("0");
            orderItem.setOrderId(orderId);
            orderItemMapper.insertSelective(orderItem);
        }

        // int i = 1/0;
        // 添加任务数据
        Task task = new Task();
        task.setCreateTime(new Date());
        task.setUpdateTime(new Date());
        task.setMqExchange(RabbitMQConfig.EX_BUYING_ADDPOINTUSER);
        task.setMqRoutingkey(RabbitMQConfig.CG_BUYING_ADDPOINT_KEY);

        Map map = new HashMap();
        map.put("username", order.getUsername());
        map.put("orderId", orderId);
        map.put("point", order.getPayMoney());
        task.setRequestBody(JSON.toJSONString(map));
        taskMapper.insertSelective(task);


        // 库存递减
        skuFeign.decrCount(order.getUsername());

        // 删除Redis中的购物车
        redisTemplate.delete("cart_" + order.getUsername());

        return orderId;
    }


    /**
     * 修改
     *
     * @param order
     */
    @Override
    public void update(Order order) {
        orderMapper.updateByPrimaryKey(order);
    }


    /**
     * 删除
     *
     * @param id
     */
    @Override
    public void delete(String id) {
        orderMapper.deleteByPrimaryKey(id);
    }


    /**
     * 条件查询
     *
     * @param searchMap
     * @return
     */
    @Override
    public List<Order> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return orderMapper.selectByExample(example);
    }

    /**
     * 分页查询
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public Page<Order> findPage(int page, int size) {
        PageHelper.startPage(page, size);
        return (Page<Order>) orderMapper.selectAll();
    }

    /**
     * 条件+分页查询
     *
     * @param searchMap 查询条件
     * @param page      页码
     * @param size      页大小
     * @return 分页结果
     */
    @Override
    public Page<Order> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page, size);
        Example example = createExample(searchMap);
        return (Page<Order>) orderMapper.selectByExample(example);
    }

    /**
     * 修改订单状态
     *
     * @param orderId
     * @param transactionId
     */
    @Override
    public void updatePayStatus(String orderId, String transactionId) {
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (order != null && "0".equals(order.getPayStatus())) { // 订单存在且未支付
            order.setPayStatus("1");
            order.setOrderStatus("1");
            order.setUpdateTime(new Date());
            order.setPayTime(new Date());
            order.setTransactionId(transactionId); // 微信返回的交易流水号
            orderMapper.updateByPrimaryKeySelective(order);

            // 订单记录变动日志
            OrderLog orderLog = new OrderLog();
            orderLog.setId(idWorker.nextId() + "");
            orderLog.setOperater("system");
            orderLog.setOperateTime(new Date());
            orderLog.setOrderStatus("1");
            orderLog.setPayStatus("1");
            orderLog.setRemarks("支付流水号" + transactionId);
            orderLog.setOrderId(order.getId());
            orderLogMapper.insert(orderLog);
        }
    }

    /**
     * 构建查询对象
     *
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap) {
        Example example = new Example(Order.class);
        Example.Criteria criteria = example.createCriteria();
        if (searchMap != null) {
            // 订单id
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                criteria.andEqualTo("id", searchMap.get("id"));
            }
            // 支付类型，1、在线支付、0 货到付款
            if (searchMap.get("payType") != null && !"".equals(searchMap.get("payType"))) {
                criteria.andEqualTo("payType", searchMap.get("payType"));
            }
            // 物流名称
            if (searchMap.get("shippingName") != null && !"".equals(searchMap.get("shippingName"))) {
                criteria.andLike("shippingName", "%" + searchMap.get("shippingName") + "%");
            }
            // 物流单号
            if (searchMap.get("shippingCode") != null && !"".equals(searchMap.get("shippingCode"))) {
                criteria.andLike("shippingCode", "%" + searchMap.get("shippingCode") + "%");
            }
            // 用户名称
            if (searchMap.get("username") != null && !"".equals(searchMap.get("username"))) {
                criteria.andLike("username", "%" + searchMap.get("username") + "%");
            }
            // 买家留言
            if (searchMap.get("buyerMessage") != null && !"".equals(searchMap.get("buyerMessage"))) {
                criteria.andLike("buyerMessage", "%" + searchMap.get("buyerMessage") + "%");
            }
            // 是否评价
            if (searchMap.get("buyerRate") != null && !"".equals(searchMap.get("buyerRate"))) {
                criteria.andLike("buyerRate", "%" + searchMap.get("buyerRate") + "%");
            }
            // 收货人
            if (searchMap.get("receiverContact") != null && !"".equals(searchMap.get("receiverContact"))) {
                criteria.andLike("receiverContact", "%" + searchMap.get("receiverContact") + "%");
            }
            // 收货人手机
            if (searchMap.get("receiverMobile") != null && !"".equals(searchMap.get("receiverMobile"))) {
                criteria.andLike("receiverMobile", "%" + searchMap.get("receiverMobile") + "%");
            }
            // 收货人地址
            if (searchMap.get("receiverAddress") != null && !"".equals(searchMap.get("receiverAddress"))) {
                criteria.andLike("receiverAddress", "%" + searchMap.get("receiverAddress") + "%");
            }
            // 订单来源：1:web，2：app，3：微信公众号，4：微信小程序  5 H5手机页面
            if (searchMap.get("sourceType") != null && !"".equals(searchMap.get("sourceType"))) {
                criteria.andEqualTo("sourceType", searchMap.get("sourceType"));
            }
            // 交易流水号
            if (searchMap.get("transactionId") != null && !"".equals(searchMap.get("transactionId"))) {
                criteria.andLike("transactionId", "%" + searchMap.get("transactionId") + "%");
            }
            // 订单状态
            if (searchMap.get("orderStatus") != null && !"".equals(searchMap.get("orderStatus"))) {
                criteria.andEqualTo("orderStatus", searchMap.get("orderStatus"));
            }
            // 支付状态
            if (searchMap.get("payStatus") != null && !"".equals(searchMap.get("payStatus"))) {
                criteria.andEqualTo("payStatus", searchMap.get("payStatus"));
            }
            // 发货状态
            if (searchMap.get("consignStatus") != null && !"".equals(searchMap.get("consignStatus"))) {
                criteria.andEqualTo("consignStatus", searchMap.get("consignStatus"));
            }
            // 是否删除
            if (searchMap.get("isDelete") != null && !"".equals(searchMap.get("isDelete"))) {
                criteria.andEqualTo("isDelete", searchMap.get("isDelete"));
            }

            // 数量合计
            if (searchMap.get("totalNum") != null) {
                criteria.andEqualTo("totalNum", searchMap.get("totalNum"));
            }
            // 金额合计
            if (searchMap.get("totalMoney") != null) {
                criteria.andEqualTo("totalMoney", searchMap.get("totalMoney"));
            }
            // 优惠金额
            if (searchMap.get("preMoney") != null) {
                criteria.andEqualTo("preMoney", searchMap.get("preMoney"));
            }
            // 邮费
            if (searchMap.get("postFee") != null) {
                criteria.andEqualTo("postFee", searchMap.get("postFee"));
            }
            // 实付金额
            if (searchMap.get("payMoney") != null) {
                criteria.andEqualTo("payMoney", searchMap.get("payMoney"));
            }

        }
        return example;
    }

}
