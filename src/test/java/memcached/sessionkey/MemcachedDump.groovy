package memcached.sessionkey

import com.danga.MemCached.*
import com.whalin.MemCached.MemCachedClient
import com.whalin.MemCached.SockIOPool

// memcachedに接続
siop = SockIOPool.getInstance()
siop.setServers(["192.168.1.178:11211"] as String[])
siop.initialize()
mcc = new MemCachedClient()

slabs = [] as Set
servers = mcc.statsSlabs()
servers.each { server, stats ->
  // slab番号を取得
  stats.each { key, value ->
    if( key.indexOf(':') != -1 ){
      slabs.add(Integer.parseInt(
        key.substring(0, key.indexOf(':'))
      ))
    }
  }
}
for( slab in slabs ){
  // キャッシュダンプ
  cdr = mcc.statsCacheDump(slab, 10000/*= limit */)
  cdr.each { server, caches ->
    println "server : ${server}"
    caches.each { key, value ->
      println "$key : $value"
    }
  }
}