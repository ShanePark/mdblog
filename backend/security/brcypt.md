# BCryptPasswordEncoder 사용시 인코딩 할때마다 결과가 달라져요

## Intro

BCryptPasswordEncoder를 빈에 등록 해놓고, 암호를 검증하는 AuthenticationProvider를 별도로 구현해 봐야 할 일이 있어서 구현을 하고 있었는데요, 아무리 비밀번호를 올바르게 입력 해도 자꾸 비밀번호가 틀리다고 나오는 문제가 발생했습니다.

디버그를 하다 보니 분명 입력된 암호는 같았는데요. 혹시나 각기 다른 비밀번호 인코더가 동작하는지 의심되어서 각각 암호를 저장 할 때와, 암호를 검증 할 때 사용하는 비밀번호 인코더를 확인 해 보았습니다. 확인 결과 완전히 동일한 객체인게 확인 되어서 혹시나 싶어 각각의 위치에서 "1234" 를 인코딩 하도록 해 보았는데요 결과가 당황스러웠습니다.

```java
@Test
public void encoderTest() {
  BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

  String pass1 = encoder.encode("1234");
  String pass2 = encoder.encode("1234");

  System.out.println("pass1 = " + pass1);
  System.out.println("pass2 = " + pass2);
  assertThat(pass1).isEqualTo(pass2);
}
```

같은 인코더로 같은 비밀번호를 인코딩 하기 때문에 같은 결과가 나와야 한다고 생각하고, equals로 암호 검증을 시도 하고 있었거든요.

그런데 위의 테스트 코드를 돌려 보면

![image-20221007224317782](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/security/brcypt.assets/image-20221007224317782.png)

분명 똑같은 `1234` 를 같은 인코더로 연속해서 인코딩 했는데, 전혀 다른 결과물이 발생했습니다.

## Bcrypt

불과 몇년 전 까지만 해도 단방향 해시함수 `SHA-1` 이 대부분의 사이트에서 비밀번호를 비롯한 여러 용도로 쓰이고 있었습니다. 

"암호화 되며 원본 데이터가 손상되기 때문에 복호화가 불가능하다" 라는 개념이 얼마나 매력적인지 저도 처음으로 진행 해보는 학원 팀 프로젝트에서 SHA 암호화를 적용 한 뒤에 완벽한 보안이 준비되었다고 착각하던게 불과 얼마 전 이었습니다.

**하지만  단방향 해시 함수에는 몇가지 치명적인 문제가 있습니다.**

해시 함수의 빠른 처리속도로 인해 오히려 공격자가 매우 빠른속도의 임의의 문자열의 다이제스트(해시값)과 대상의 다이제스트를 비교 할 수 있다는 건데요, 그렇기에 애초에 메시지 인증이나 무결성 체크를 위한 것이지 패스워드 인증에는 적합 하지 않습니다.

특히나 컴퓨터의 연산능력이 갈수록 급격히 좋아지며 단방향 해시 함수들도 하나씩 무너져 버리고 있습니다.

이를 보완하기 위해 원본 메시지에 문자열을 추가해 해시값을 생성하는 솔팅(salting)이나, 패스워드의 다이제스트를 생성 하고 그 생성된 다이제스트를 입력값으로 하여 또 다이제스트를 생성하는 작업을 반복하는 키 스트레칭등이 사용 되고 있습니다.

그 중 Blowfish 암호에 기반을 두어 애초에 패스워드에 사용될 목적으로 만들어진 **Brcypt**는 레인보우 테이블 공격 방지를 위해 솔트를 통합했을 뿐만 아니라 시간에 지남에 따라 반복 횟수를 늘려서 속도를 느리게 만들어 향상된 컴퓨터들의 연산 능력에도 저항이 생겼습니다.

![image-20221007232351011](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/security/brcypt.assets/image-20221007232351011.png)

> https://en.wikipedia.org/wiki/Bcrypt

## 문제 원인

위에서 알아 본 것 처럼 Bcrypt는 매번 내부적으로 임의의 salt를 이용해 다이제스트를 만들고 있는데요, 그렇기 때문에 인코딩 할 때 마다 매번 다른 값이 나옵니다. 

결과적으로 충분히 긴 salt로 Rainbow Table 공격에 대한 저항성도 가지고 해시충돌에 대한 방어도 이루어지고 있습니다.

다만, 그래서 단순 해시 비교로는 암호의 일치 여부를 확인 할 수가 없습니다.

## 해결

BCryptPasswordEncoder에서는 matches 메서드를 제공하기 때문에이를 통해서 일치 여부를 확인 할 수 있습니다.

```java
package kr.quidev.passwordencoder;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

public class EncoderTest {

    @Test
    public void encoderTest() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String pass2 = encoder.encode("1234");
        assertThat(encoder.matches("1234", pass2)).isTrue();
    }
}

```

위에서의 코드 처럼 `matches` 메서드를 호출해 비교하고자 하는 대상의 raw 데이터와 암호화되어 저장된 값을 비교 해주면 됩니다.

![image-20221015090539023](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/security/brcypt.assets/image-20221015090539023.png)

> 194 ms 가 걸려 비밀번호가 일치한다는 걸 확인 해 주었습니다.

`matches` 메서드를 따라가보면 여러가지 복잡한 연산을 통해 일치 여부를 확인 해 주는 것을 확인 할 수 있습니다.

![image-20221015090933508](https://raw.githubusercontent.com/Shane-Park/mdblog/main/backend/security/brcypt.assets/image-20221015090933508.png)

결론적으로 Bcrypt는 단순 암호화된 값의 비교를 하면 안되고 일치 여부를 확인해주는 과정이 필요함을 알 수 있었습니다.

이상입니다. 

**References**

- https://d2.naver.com/helloworld/318732
- https://en.wikipedia.org/wiki/Bcrypt
- https://groups.google.com/g/ksug/c/1W11JJ6AZxc?pli=1