package com.consistenthash;

import java.util.*;

public class ConsistentHash {
    //物理节点集合
    private List<String> realNodes =new ArrayList<>();
    //物理节点与虚拟节点的对应关系，存储的是虚拟节点的hash值
    private Map<String,List<Integer>> real2VirtualMap =new HashMap<>();
    //虚拟节点的个数
    private int virtualNum =100;
    //排序存储结果 红黑树，key是虚拟节点的hash值，value是物理节点
    private SortedMap<Integer,String> sortedMap =new TreeMap<>();

    public ConsistentHash(int virtualNum) {
        super();
        this.virtualNum = virtualNum;
    }
    public ConsistentHash() {
    }
    //加入服务器的方法
    public void addServer(String node){
        this.realNodes.add(node);

        String vnode =null;

        int i =0,count =0;
        List<Integer> virtualNodes =new ArrayList<>();
        this.real2VirtualMap.put(node,virtualNodes);

        //创建虚拟节点，并放到环上（排序存储）
        while (count<this.virtualNum ){
            i++;
            vnode =node+"&&"+i;
            int hashValue =FLV1_32_HASH.getHash(vnode);
            if (!this.sortedMap.containsKey(hashValue)){
                virtualNodes.add(hashValue);
                this.sortedMap.put(hashValue,node);
                count++;
            }
        }
    }
    //找到数据库的存放节点
    public String getServer(String key){
        int hash =FLV1_32_HASH.getHash(key);
        //得到大于该hash值的所有虚拟节点map
        SortedMap<Integer,String> subMap =sortedMap.tailMap(hash);
        //取第一个key
        Integer vhash =subMap.firstKey();
        //返回对应的服务器
        return  subMap.get(vhash);
    }
    //删除一个物理节点
    public void removeServer(String node){
        List<Integer> virtualNodes =this.real2VirtualMap.get(node);
        if(virtualNodes!=null ){
            for (Integer hash:virtualNodes){
                this.sortedMap.remove(hash);
            }
            this.real2VirtualMap.remove(node);
            this.realNodes.remove(node);
        }
    }

    /**一致性hash算法+虚拟节点（实现步骤）
     * 1.物理节点
     * 2.虚拟节点
     * 3.hash算法
     * 4.虚拟节点放到环上
     * 数据找到对应的虚拟节点
     * @param args
     */
    public static void main(String[] args) {
        ConsistentHash ch =new ConsistentHash();
        ch.addServer("192.168.108.10");
        ch.addServer("192.168.108.11");
        ch.addServer("192.168.108.11");

        ch.getServer("");
    }
}
