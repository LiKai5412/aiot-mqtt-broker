package com.sunvalley.aiot.mqtt.broker.path;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 缓存操作
 *
 * @author kai.li
 **/
@Slf4j
public class TopicMap<V> {

    private ConcurrentHashMap<String, Node<String, V>> datas = new ConcurrentHashMap<>();

    public boolean putData(String[] topic, V v) {
        if (topic.length == 1) {
            Node<String, V> kvNode = buildOne(topic[0], v);
            if (kvNode != null && kvNode.topic.equals(topic[0])) {
                return true;
            }
        } else {
            Node<String, V> kvNode = buildOne(topic[0], null);
            for (int i = 1; i < topic.length; i++) {
                if (i == topic.length - 1) {
                    kvNode = kvNode.putNextValue(topic[i], v);
                } else {
                    kvNode = kvNode.putNextValue(topic[i], null);
                }
            }
        }
        return true;
    }

    public boolean delete(String[] ks, V v) {
        if (ks.length == 1) {
            return datas.get(ks[0]).delValue(v);
        } else {
            Node<String, V> kvNode = datas.get(ks[0]);
            for (int i = 1; i < ks.length && kvNode != null; i++) {
                kvNode = kvNode.getNext(ks[i]);
            }
            return kvNode.delValue(v);

        }
    }

    public List<V> getData(String[] ks) {
        if (ks.length == 1) {
            return datas.get(ks[0]) == null ? null : datas.get(ks[0]).get();
        } else {
            Node<String, V> node = datas.get(ks[0]);
            if (node != null) {
                List<V> all = new ArrayList<>();
                all.addAll(node.get());
                for (int i = 1; i < ks.length && node != null; i++) {
                    node = node.getNext(ks[i]);
                    if (node == null) {
                        break;
                    }
                    all.addAll(node.get());
                }
                return all;
            }
            return null;
        }
    }

    public Node<String, V> buildOne(String k, V v) {

        Node<String, V> node = this.datas.computeIfAbsent(k, key -> {
            Node<String, V> kObjectNode = new Node<>(k);
            return kObjectNode;
        });
        if (v != null) {
            node.put(v);
        }
        return node;
    }


    class Node<K, V> {

        private final K topic;


        private volatile ConcurrentHashMap<K, Node<K, V>> map = new ConcurrentHashMap<>();


        List<V> vs = new CopyOnWriteArrayList<>();


        public K getTopic() {
            return topic;
        }

        Node(K topic) {
            this.topic = topic;
        }

        public boolean delValue(V v) {
            return vs.remove(v);
        }

        public Node<K, V> putNextValue(K k, V v) {
            Node<K, V> kvNode = map.computeIfAbsent(k, key -> {
                Node<K, V> node = new Node<>(k);
                return node;
            });
            if (v != null) {
                kvNode.put(v);
            }
            return kvNode;
        }


        public Node<K, V> getNext(K k) {
            return map.get(k);
        }


        public boolean put(V v) {
            return vs.add(v);
        }


        public List<V> get() {
            return vs;
        }
    }

    public static void main(String[] args) {
        TopicMap topicMap = new TopicMap();
        topicMap.putData(List.of("1", "2", "3").toArray(String[]::new), "a");
        topicMap.putData(List.of("4", "5", "6").toArray(String[]::new), "b");
        topicMap.putData(List.of("7", "8", "9").toArray(String[]::new), "c");
        topicMap.putData(List.of("3", "2", "3").toArray(String[]::new), "d");
        topicMap.getData(List.of("#", "2", "3").toArray(String[]::new)).forEach(o -> System.out.println(o));
    }

}
