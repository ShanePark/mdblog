# [SpringBoot] 에러 발생시 Slack으로 알림 보내기

## Intro

토이프로젝트로 단순하게 만들어서 배포 해둔 근무 및 스케줄 관리 웹 어플리케이션이 있습니다.

- 와이프가 저처럼 매일 매일 출근시간이 정해진게 아니고 쉬프트를 받아 근무를 하다 보니, 종종 근무시간을 까먹고 지각하는 경우가 있었습니다.
- 제가 와이프 근무시간을 확인하려면 항상 카톡 대화방에 들어가서 사진첩을 뒤적거려야 하는 불편함이 있었습니다.

이 두가지를 해소하고자 첫 버전을 하루만에 대충 만들고 클라우드에 배포까지 끝냈었는데, 동생도 쓰고 무엇보다 처제가 굉장히 유용하게 사용하고 기능 추가 요청도 많이 해주다 보니 처음 계획보다 확장이 꽤나 일어 나고 있습니다.

이 프로젝트 뿐만 아니라, 취업 전에 국비학원에서 진행했던 최종 프로젝트도 생각보다 참고용으로 방문해주는 분들이 있다 보니 지금까지 서버를 내리지 않고 운영중인데 어느덧 1년 4개월이 지나다 보니 여러가지 예기치못한 상황들이 생기며 에러 관제에 대한 필요성이 느껴졌습니다.

수시로 상태나 에러 로그를 확인하기 위해 서버에 여러가지 alias 등을 걸어놓고 종종 접속해 간단히 로그 등을 체크 하곤 했지만, 알림 시스템을 두는 편이 훨씬 좋겠다는 생각이 예전부터 있었고 이제는 행동으로 옮길 때가 왔습니다.

## 워크스페이스 및 채널 생성

일단 저는 워크스페이스 부터 새로이 만들었지만, 기존에 사용중인걸 쓰셔도 무방 합니다.

![image-20221025214957070](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/monitoring/slack-webhook.assets/image-20221025214957070.png)

> dutypark 이라는 이름의 워크스페이스를 만들었습니다.

이후에는 에러 알림을 받기 위한 별도의 채널을 생성 합니다.

![image-20221025215252593](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/monitoring/slack-webhook.assets/image-20221025215252593.png)

> error-log 라는 이름으로 생성 하였습니다.

채널이 생성이 되었으면, 우클릭 후  `View channel details`를 클릭 해 상세 정보 페이지로 이동 합니다.

![image-20221025215503005](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/monitoring/slack-webhook.assets/image-20221025215503005.png)

> 두 번째 메뉴에 있습니다.

## Webhooks 추가

상세 정보에서, Integrations 항목에 들어가면, App을 추가 할 수 있습니다.

![image-20221025215551668](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/monitoring/slack-webhook.assets/image-20221025215551668.png)

> 중간에 Apps가 있습니다.

`Add an App`을 클릭 해 이동 합니다.

![image-20221025215711594](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/monitoring/slack-webhook.assets/image-20221025215711594.png)

그러면 굉장히 많은 앱들이 나옵니다. 예전에 학원 팀원들과 프로젝트를 할 때는 여기에서 Github 앱을 추가해서 commit 이나 Pull Requrest 등을 확인 했던 기억이 있습니다.

Webhook을 검색 해 줍니다.

![image-20221025215813329](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/monitoring/slack-webhook.assets/image-20221025215813329.png)

그러면 Incoming 과 Outgoing Webhook이 보입니다. 우리는 슬랙을 통해 알림을 받을 것 이기 때문에 Incoming WebHooks를 Install 해줍니다.



![image-20221025215925705](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/monitoring/slack-webhook.assets/image-20221025215925705.png)

> 아까 추가해 둔 error-log 채널에 Incoming Webhooks integration을 추가 해 줍니다.

추가를 해 주면 아래 보이는 것 처럼 Webhook URL 및 사용 방법에 대한 안내를 해 줍니다.

![image-20221025220027029](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/monitoring/slack-webhook.assets/image-20221025220027029.png)

## Webhook으로 알림 전송

Sending Messages 라며 웹훅을 통해 알림을 전송하는 방법에 대한 설명이 써있는데요, 그대로 따라 해서 잘 작동하는지 테스트를 진행 해 보도록 하겠습니다.

설명에 따르면 Webhook URL로 데이터를 전송 하는 방법은 2가지가 있다고 하는데요

- POST 요청에 JSON 문자열을 payload 파라미터 형태로 전송
- POST 요청에 JSON 문자열을 body로 전송

간단하게 curl로 테스트 해볼 수 있을 것 같네요 하단에 있는 Exaple 요청을 그대로 전송 해 봅니다.

```bash
curl -X POST --data-urlencode "payload={\"channel\": \"#error-log\", \"username\": \"webhookbot\", \"text\": \"This is posted to #error-log and comes from a bot named webhookbot.\", \"icon_emoji\": \":ghost:\"}" https://hooks.slack.com/services/[주소]
```

![image-20221025222111481](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/monitoring/slack-webhook.assets/image-20221025222111481.png)

> ok 응답이 나옵니다.

Slack 에서도 메시지가 도착 한 것을 확인 해 봅니다.

![image-20221025222209757](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/monitoring/slack-webhook.assets/image-20221025222209757.png)

> 정상적으로 알림 메시지가 전달 되었습니다.

## SpringBoot 설정

이제 스프링 부트에서 에러가 발생 했을 때 웹훅으로 슬랙에 알림 메시지를 보내도록 설정만 해주면 되겠습니다.

물론 Slack Webhook으로 원하는 요청을 보내는 작업을 직접 구현 해도 좋지만, 다른 사용자들이 사용하기 좋게 만들어 Github에 올려 둔 프로젝트들이 많이 있기 때문에 일단 몇 가지 사용을 해 보고, 불편함이나 한계가 있다면 그때 새로 구현하거나 fork 해 보려고 합니다.

제가 찾은 프로젝트는 아래와 같습니다.

- https://github.com/gpedro/slack-webhook
- https://github.com/maricn/logback-slack-appender

이 중 저는 메시지 부분은 직접 구현하기 위해 `slack-webhook`을 사용했습니다.

### 의존성 추가

**build.gradle.kts**

```kotlin
implementation("net.gpedro.integrations.slack:slack-webhook:1.4.0")
```

의존성을 추가 해 줍니다.

바로 테스트 코드를 작성 해서, 메시지 전송을 확인 해 봅니다. 사용법은 간단 합니다.

![image-20221025225222302](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/monitoring/slack-webhook.assets/image-20221025225222302.png)

> https://github.com/gpedro/slack-webhook

```kotlin
package com.tistory.shanepark.dutypark.common

import net.gpedro.integrations.slack.SlackApi
import net.gpedro.integrations.slack.SlackMessage
import org.junit.jupiter.api.Test

class WebhookTest {

    @Test
    fun test() {
        val token = "token key..."
        val api = SlackApi("https://hooks.slack.com/services/$token")
        api.call(SlackMessage("Hello SpringBoot Test!"))
    }
    
}
```

코드를 실행 해 보면

![image-20221025224610737](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/monitoring/slack-webhook.assets/image-20221025224610737.png)

> 메시지가 정확히 전달 되었습니다!

### 컨트롤러 어드바이스 추가

이제 에러가 발생시 에러를 감지해서 슬랙 메시지를 보낼 방법을 생각해야 합니다.

POST 요청의 바디를 inputstream이 한번 읽고 마는 걸 방지하기 위해 필터를 적용해두고,  slf4j의 MDC 를 사용해 요청별로 로그데이터를 활용하는 등 알림으로 보낼 정보를 만들기 위한 다양한 방법이 있지만 일단 가장 기초적인 방법으로 접근 해 보려고 합니다.

바로 컨트롤러 어드바이스인데요, 스프링의 다양한 예외 처리 방법 중에서 사용하기에 간단하고 활용도가 좋기 때문에 누구나 한번쯤 사용 해 봤을 법 하여 선택했습니다. 단순하게 에러 로그만 담을 수 있지만, `gpedro/slack-webhook` 에 있는 다양한 API를 활용 해 보기 위해 좀 더 메시지에 정보를 담아서 작성 해 보았습니다.

**ErrorDetectorAdvisor.kt**

```kotlin
package com.tistory.shanepark.dutypark.common.slack.advice

import net.gpedro.integrations.slack.SlackApi
import net.gpedro.integrations.slack.SlackAttachment
import net.gpedro.integrations.slack.SlackField
import net.gpedro.integrations.slack.SlackMessage
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.util.*
import javax.servlet.http.HttpServletRequest

@ControllerAdvice
class ErrorDetectAdvisor(
    private val slackApi: SlackApi,
) {

    @ExceptionHandler(Exception::class)
    fun handleException(req: HttpServletRequest, e: Exception) {

        val slackAttachment = SlackAttachment()
        slackAttachment.setFallback("Error")
        slackAttachment.setColor("danger")
        slackAttachment.setTitle("Error Detect")
        slackAttachment.setTitleLink(req.contextPath)
        slackAttachment.setText(e.stackTraceToString())
        slackAttachment.setColor("danger")
        slackAttachment.setFields(
            listOf(
                SlackField().setTitle("Request URL").setValue(req.requestURL.toString()),
                SlackField().setTitle("Request Method").setValue(req.method),
                SlackField().setTitle("Request Time").setValue(Date().toString()),
                SlackField().setTitle("Request IP").setValue(req.remoteAddr),
                SlackField().setTitle("Request User-Agent").setValue(req.getHeader("User-Agent")),
            )
        )

        val slackMessage = SlackMessage()
        slackMessage.setAttachments(Collections.singletonList(slackAttachment))
        slackMessage.setIcon(":ghost:")
        slackMessage.setText("Error Detect")
        slackMessage.setUsername("DutyPark")

        slackApi.call(slackMessage)
        throw e
    }

}

```

코드를 확인 하면 단순하기 때문에 어렵지 않게 흐름을 파악 하실 수 있습니다.

일단 예외의 최상위인 `Exception`을 모조리 핸들링하는 ExceptionHandler를 선언 하고, HttpServletRequest와 Exception 에서 얻어낼 수 있는 정보 중 유용한 몇가지를 추려 슬랙 메시지로 만들었습니다.

그리고 최종적으로 주입받은 slackapi 로 call 을 발생합니다.

그리고 잡아낸 예외는 처리할 목적이 아니기 때문에 그대로 다시 던져 주었습니다.

SlackApi는 아래와 같이 bean으로 등록 해 두었습니다.

```kotlin
@Configuration
class SlackLogAppenderConfig {

    @Value("\${dutypark.slack.token}")
    lateinit var token: String

    @Bean
    fun slackApi(): SlackApi {
        return SlackApi("https://hooks.slack.com/services/$token")
    }

}
```

### 확인

이제 에러를 발생시켜 슬랙 메시지가 정상적으로 전달이 되는지 확인을 해 보도록 하겠습니다.

일단 컨트롤러에 에러를 발생시킬 매핑을 하나 추가 해 임의로 예외를 발생 시켜 보겠습니다.

```kotlin
@GetMapping("/error1")
fun error(): String {
  throw Exception("test")
}
```

위의 코드 추가 후, `localhost:8080/error1` 로 요청을 날려 보겠습니다.

![image-20221026212944698](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/monitoring/slack-webhook.assets/image-20221026212944698.png)

> 일단 에러 페이지를 보여줍니다.

![image-20221026213016984](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/monitoring/slack-webhook.assets/image-20221026213016984.png)

> 로그에도 에러의 흔적이 있습니다.

이제 슬랙을 확인 해 보면

![image-20221026214017676](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/monitoring/slack-webhook.assets/image-20221026214017676.png)

전체적인 에러 스택트레이스와 함께, 요청 URL 및 메서드, 그리고 요청 시간과 요청 IP 주소 및 User-Agent 정보를 보여줍니다.

일단 기본적인 에러 관제 시스템은 갖춰진 셈 입니다.

## AOP

혹은 특정한 작업을 수행 할 때 마다 로그를 남기듯 알림을 보내도록 설정 할 수도 있겠습니다. 원하는 이벤트들을 쉽게 묶기 위해 AOP를 활용 해 보도록 하겠습니다.

일단 슬랙으로 알림을 보내는 상황들을 쉽게 마킹 하기 위해 어노테이션을 생성 해 주었습니다.

**SlackNotification.kt**

```kotlin
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class SlackNotification
```

그리고 이제 Aspect를 추가 해 줍니다.

```kotlin
package com.tistory.shanepark.dutypark.common.slack.aspect

import net.gpedro.integrations.slack.SlackApi
import net.gpedro.integrations.slack.SlackAttachment
import net.gpedro.integrations.slack.SlackField
import net.gpedro.integrations.slack.SlackMessage
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class SlackNotificationAspect(
    private val slackApi: SlackApi
) {

    @Around("@annotation(com.tistory.shanepark.dutypark.common.slack.annotation.SlackNotification)")
    fun slackNotification(proceedingJoinPoint: ProceedingJoinPoint): Any? {
        val result = proceedingJoinPoint.proceed()

        val slackAttachment = SlackAttachment()
        slackAttachment.setFallback("Post")
        slackAttachment.setColor("good")
        slackAttachment.setTitle("Data save detected")
        slackAttachment.setFields(
            listOf(
                SlackField().setTitle("Arguments").setValue(proceedingJoinPoint.args.joinToString()),
                SlackField().setTitle("method").setValue(proceedingJoinPoint.signature.name),
            )
        )

        val slackMessage = SlackMessage()
        slackMessage.setAttachments(listOf(slackAttachment))
        slackMessage.setIcon(":floppy_disk:")
        slackMessage.setText("Post Request")
        slackMessage.setUsername("DutyPark")
        slackApi.call(slackMessage)

        return result
    }

}

```

어노테이션이 달린 메서드들을 수행 한 후에 메서드의 argument 들을 슬랙 메시지로 보내도록 하는 간단한 Aspect 입니다.

그리고는 이제 알람을 원하는 메서드들에 어노테이션을 달아 줍니다.

**DutyApiController.kt**

```kotlin
package com.tistory.shanepark.dutypark.duty.controller

import com.tistory.shanepark.dutypark.common.slack.annotation.SlackNotification
import com.tistory.shanepark.dutypark.duty.domain.dto.DutyUpdateDto
import com.tistory.shanepark.dutypark.duty.domain.dto.MemoDto
import com.tistory.shanepark.dutypark.duty.service.DutyService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/duty")
class DutyApiController(
    private val dutyService: DutyService
) {

    @PutMapping("update")
    @SlackNotification
    fun updateDuty(@RequestBody dutyUpdateDto: DutyUpdateDto): ResponseEntity<Boolean> {
        dutyService.update(dutyUpdateDto)
        return ResponseEntity.ok(true)
    }

    @PutMapping("memo")
    @SlackNotification
    fun updateMemo(@RequestBody memoDto: MemoDto): ResponseEntity<Boolean> {
        dutyService.updateMemo(memoDto)
        return ResponseEntity.ok(true)
    }

}
```

이후 테스트를 해 보면

![image-20221026222120526](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/monitoring/slack-webhook.assets/image-20221026222120526.png)

의도한 대로 잘 작동 합니다.

## 별도 쓰레드에서 처리

다만, 슬랙을 웹 훅에 요청을 보내고, 그 응답을 받기까지의 시간을 비즈니스 로직이 함께 기다리는건 뭔가 공평하지 않습니다.

비즈니스 로직은 본인이 처리할 내용만 완료 하고 응답을 바로 보내야 하는데, 네트워크를 오고 가는 슬랙 웹훅이 그 과정에 낀다면 응답 시간이 굉장히 많이 늘어나게 됩니다. 슬랙 알림 요청의 경우에는 그래서 별도의 쓰레드에서 처리하는게 좋겠습니다.

![image-20221106221709211](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/monitoring/slack-webhook.assets/image-20221106221709211.png)

> 응답 시간 확인

일단 슬랙 요청이 같은 쓰레드에서 순차적으로 처리 되었을 경우의 응답 시간을 먼저 체크 해 보았습니다.

평균 800 ms 가량이 나오고 있습니다.

### TaskExecutor Bean 등록

일단 스프링이 제공하는 TaskExecutor를 Bean으로 등록 해 줍니다. TaskExecutor 구현체는 아래와 같이 여러개가 있는데요

![image-20221106222716985](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/monitoring/slack-webhook.assets/image-20221106222716985.png)

일반적으로 많이 사용하는 ThreadPoolTaskExecutor 를 등록 해서 사용 해 보도록 하겠습니다.

```kotlin
@Bean
fun threadPoolTaskExecutor(): TaskExecutor {
    val executor = ThreadPoolTaskExecutor()
    executor.corePoolSize = 5
    executor.maxPoolSize = 5
    executor.initialize()
    return executor
}
```

### 비동기 호출

이제 빈으로 주입한 TaskExecutor를 의존하도록 한 뒤에, 이를 이용해 비동기 호출을 합니다.

```kotlin
@Aspect
@Component
class SlackNotificationAspect(
    private val slackApi: SlackApi,
    private val taskExecutor: TaskExecutor,
) {
  
  @Around("@annotation(com.tistory.shanepark.dutypark.common.slack.annotation.SlackNotification)")
  fun slackNotification(proceedingJoinPoint: ProceedingJoinPoint): Any? {
    ...
    taskExecutor.execute {
      slackApi.call(slackMessage)
    }

    return proceedingJoinPoint.proceed()
  }

}
```

이제 슬랙 알림에 대한 웹훅은 신경쓰지 않고 로직이 진행 됩니다. 응답시간을 확인 해 보겠습니다.

![image-20221106224833358](https://raw.githubusercontent.com/Shane-Park/mdblog/main/devops/monitoring/slack-webhook.assets/image-20221106224833358.png)

> 응답에 걸리는 시간이 절반으로 줄어들었습니다.  

## 마치며

아주 간단한 방법으로 에러 관제하는 방법에 대해서 알아 보았습니다.

사실 보다 확실하게 하기 위해서는 전달되는 요청의 body 를 포함해 더 많은 정보를 확인 할 수 있는게 좋겠고, 에러가 발생한 사용자에 대한 보다 많은 정보 및 에러에 대한 보다 자세한 정보가 있으면 좋겠지만 일단 `작동 하는` 알림 시스템을 만드는 것에 집중 해 보았습니다.

에러가 발생 했을 때, 무방비하게 당하지 않기 위해서는 토이 프로젝트라고 해도 알림 시스템은 갖추는게 좋을 거라고 생각합니다.

위의 코드는 제가 진행중인 토이 프로젝트 Github 저장소에서 확인 할 수 있으며 추후 코드가 변경 될 것에 대비해 Slack 알림 시스템을 막 추가한 커밋에서의 파일 탐색을 할 수 있는 Github 링크를 남겨 두도록 하겠습니다.

> https://github.com/Shane-Park/dutypark/tree/133ecb7b59c6771e5e75780e01a7e0dbc9dba90b

이상입니다. 

**References**

- https://api.slack.com/messaging/webhooks
- https://medium.com/chequer/springboot-slack-logback%EC%9D%84-%EC%9D%B4%EC%9A%A9%ED%95%9C-%EC%8B%A4%EC%8B%9C%EA%B0%84-%EC%97%90%EB%9F%AC-%EB%AA%A8%EB%8B%88%ED%84%B0%EB%A7%81-%EA%B5%AC%ED%98%84%ED%95%98%EA%B8%B0-7f231812d3fc