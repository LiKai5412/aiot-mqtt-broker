####本项目是网关项目，如存在阻塞调用,则可能降低整体并发和吞吐量

####【非常重要】 vertx.work线程数量,默认128
  在io处理尽量使用(非阻塞)异步方案,  
  同时避免使用锁竞争  
  
####【阻塞转Fiber参考实现】
  采用FiberHandler  
  https://vertx.io/docs/vertx-sync/java/#_using_a_code_fiberhandler_code  
  http://vertxchina.github.io/vertx-translation-chinese/reactive/Sync.html  
  https://github.com/vert-x3/vertx-examples/tree/master/sync-examples  
