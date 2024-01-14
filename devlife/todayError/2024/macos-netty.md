# Unable to load io.netty.resolver.dns.macos 에러 해결

## 문제

아래의 에러를 맞이했다.

> Unable to load io.netty.resolver.dns.macos.MacOSDnsServerAddressStreamProvider, fallback to system defaults. This may result in incorrect DNS resolutions on MacOS. Check whether you have a dependency on 'io.netty:netty-resolver-dns-native-macos'. Use DEBUG level to see the full stack: java.lang.UnsatisfiedLinkError: failed to load the required native library

한참오래전에도 스프링에서 Http interface 사용하려고 webflux 의존성 추가하다가 똑같은 문제가 있었는데, 어느정도 시간이 지났기에 지금쯤은 따로 의존성 없이 해결되지 않았을까 싶었는데 여전했다.

## 원인

Apple 의 M1, M2, M3 칩은 ARM 기반 아키텍처를 사용하며 이로인해 일부 네이티브 라이브러리가 호환되지 않거나 누락될 수 있다.

Netty는 성능상 이점을 얻기 위해 네이티브 코드를 사용해 시스템의 DNS 리졸버와 연동하는데, 위에 언급한 ARM 기반의 칩에서는 필요한 라이브러리가 없어서 이 native 라이브러리들을 직접 명시적으로 제공해줘야 한다.

## 해결

`netty-resolver-dns-native-macos` 의존성을 추가해주면 해결된다.

예를 들어 gradle을 사용중이라면 아래와 같이 추가해주면 된다. 런타임 환경에서만 해당 의존성이 필요하므로 runtimeOnly로 지정해준다.

```groovy
    runtimeOnly 'io.netty:netty-resolver-dns-native-macos:4.1.104.Final:osx-aarch_64'
```

최신 버전은 아래의 링크에서 확인하자.

https://mvnrepository.com/artifact/io.netty/netty-resolver-dns-native-macos

벌써 몇년은 지난 것 같은데, 다음번에 개발할때는 추가 안해도 자동으로 지원해주면 좋겠다.

끝

**References**

- https://randomwits.com/blog/unable-to-load-io-netty-resolver-dns-macos-macosdnsserveraddressstreamprovider
