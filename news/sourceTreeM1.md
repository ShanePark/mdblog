# SourceTree Apple Silicon 지원 소식

## Intro

정말 오래기다렸습니다. 사실 처음엔 기다린게 맞는데 진작에 포기하고 소스트리를 버린지 한참 되었습니다. 

그러던 중 오랜만에 생각이 나 https://isapplesiliconready.com/ 를 확인 해 보니 M1 Native에 초록 불이 들어와 있었습니다. 그럼에도 atlassian 사이트에는 관련 언급이 따로 없길래 잘못된 정보인가? 하며 

<img src=https://raw.githubusercontent.com/Shane-Park/mdblog/main/news/sourceTreeM1.assets/image-20220319092923565.webp width=750 height=544>

> https://isapplesiliconready.com/app/SourceTree

다운로드 받아 설치를 해 보았는데 Apple Native로 잘 실행이 되었습니다. Git을 처음 접할 때 참 도움을 많이 받은 소프트웨어기 때문에 남다른 애착이 있었는데 반가운 마음에 소식을 가지고 왔습니다.

## 1년간의 이야기

듣기로는 SourceTree Mac 버전의 코드가 주먹구구식으로 관리된지가 너무 오래되다보니 진작에 내재된 문제가 정말 많았었다고 하는데요. 그러던 중 M1 애플 실리콘의 새로운 등장 한방에 결국 속절 없이 무너져 버렸었습니다.

대다수의 기업들이 1~2개월 내에 발빠르게 Apple Silicon Native 대응을 한것과 대조적으로 소스트리는 1년이 넘도록 애플 실리콘에 대응을 못했는데요. 로제타로라도 잘 구동이 되었다면 문제없이 사용을 했겠지만 아주 심각한 결함이 있었기 때문에 도저히 참고 사용 할 수가 없었습니다. 이로인해 저도 새로산 M1 맥북에어의 배터리가 완전이 맛이 가버려서 구입한지 100일도 지나지 않아 배터리 교체를 받아야 했습니다. 그나마 무상으로 받은건 위안입니다.

그 일 이후로는 소스트리는 깔끔하게 포기하고 Github Desktop을 사용 해왔으며 rebase나 ammend 같은 지원하지 않는 기능들은 Terminal을 켜서 해결 하곤 해왔습니다.

> [Sourcetree (소스트리) 배터리 이슈, GUI Git 추천](https://shanepark.tistory.com/61)

딱 일년 전 글인데. 빠르게 포기하길 망정이지 목빠지게 기다렸으면 정말 힘들었겠습니다. 사실 왠만한 기업들이 금방금방 M1 Native 소프트웨어들을 출시해서 기다릴 법도 하지만, 이미 백개 이상의 코멘트가 달린 아래의 이슈를 확인했을때

> https://jira.atlassian.com/browse/SRCTREE-5306 

![image-20220319091216263](https://raw.githubusercontent.com/Shane-Park/mdblog/main/news/sourceTreeM1.assets/image-20220319091216263.webp)

Jira Engineering Manager인 Ranjith에 따르면 해당 이슈 해결에만 3개월 타겟을 잡고 진행중일 정도로 CPU 점유 문제가 상당히 스케일이 크다는걸 눈치 챌 수 있었고 M1 Native 까지 지원하려면 아무리 빨라도 반년 내에는 안된다고 생각하고 깔끔하게 기다림을 접었었습니다. 결국 생각보다 오래 걸렸지만 1년만에 Apple Silicon 대응 버전을 내놓기는 했네요. 물론 해당 이슈가 완전 해결되진 않았는지 [M1 Support](https://jira.atlassian.com/browse/SRCTREE-7446) 이슈는 Close가 되었어도 위의 이슈의 STATUS 는 아직 `SHORT TERM BACKLOG` 상태 입니다.

## M1 Support

### Sourcetree 4.1.6

![image-20220319092557397](https://raw.githubusercontent.com/Shane-Park/mdblog/main/news/sourceTreeM1.assets/image-20220319092557397.webp)

> https://product-downloads.atlassian.com/software/sourcetree/ReleaseNotes/Sourcetree_4.1.6.html

Apple Silicon에 대한 Native Support에 관련된 내용은 Release Note에서만 짧은 한줄로 확인 할 수 있었습니다. 대다수의 소프트웨어들은 다운 받을 때 부터 

![image-20220319092727051](https://raw.githubusercontent.com/Shane-Park/mdblog/main/news/sourceTreeM1.assets/image-20220319092727051.webp)

> IntelliJ IDEA 다운로드 페이지

Intel 칩셋과 Apple Silicon 중 선택을 해서 다운 받도록 되어 있는데, SourceTree는 따로 그런 옵션이 없어서 혹시 아직 베타 버전에만 적용 된건가 생각도 했었습니다. 그래서 직접 다운 받아 실행을 해 보았는데 

![image-20220319091737945](https://raw.githubusercontent.com/Shane-Park/mdblog/main/news/sourceTreeM1.assets/image-20220319091737945.webp)

Apple Native로 구동되는 것이 확인 되었습니다.

### CPU issue 해결 여부

하지만 여전히 CPU 점유율이 말도 안되고, 배터리를 이전 처럼 빨아들인다면 (관련 이슈에서는 drain 이라고 표현했습니다.) 사용하지 못하기 때문에 몇시간동안 Active 상태와 Idle 상태 모두 체크 해 보았습니다.

그랬더니 예전에는 금방 눈에 띄게 보였던 문제들이 적어도 제가 체크하는 동안에는 발견되지 않았습니다.

해당 이슈를 공식적으로 Close 시킨게 아니기 때문에 사소한 문제들이 남아있을 지도 모르겠으나 적어도 예전처럼 못 쓸 정도는 아니라고 판단 됩니다. 예전에는 설치 해 두기만 해도 맥북을 갉아 먹는 모양새였지만 이제는 모니터링을 한번씩 해보긴 해야 확실해지겠지만 마음 놓고 설치해 사용 해도 된다고 판단됩니다.

![image-20220319093730416](https://raw.githubusercontent.com/Shane-Park/mdblog/main/news/sourceTreeM1.assets/image-20220319093730416.webp)

> SourceTree가 Github Desktop에 비해 여러가지 장점이 있지만 그중 Branch 들을 그래프로 보기 좋게 정리해주는게 가장 그리웠습니다. 그 외 커밋 옵션 선택 등 보다 강력한 기능들이 꽤 많습니다.

지금은 Github Desktop도 오픈소스의 장점을 살려 1년간 몇시간마다 이뤄지는 commit&merge 그리고 매주 진행되는 업데이트를 통해 수많은 기능이 추가 되었기 때문에 충분히 쓸만한 프로그램이 되었습니다.

하지만 역시 Git이라는 전대미문의 진입장벽을 가진 도구를 초보자도 쉽게 접근 할 수 있으며 충분히 강력한 기능을 가지고 있는 SourceTree가 다시 선택지 안으로 들어왔다는건 정말 반가운 소식 입니다. 1년만에 소스트리는 써 보니 국비학원 다닐 때 혼자 끙끙대며 처음 Git을 익힐 때와 팀원들에게 쉽게 설명하려고 하다 결국 블로그에 처음으로 글다운 글을 작성해 보았던 때도 기억도 나네요. 

4.1.6 버전이 출시된지도 이제 한달이 지났기 때문에 어느정도 안정화 되었을 거라 생각됩니다. 그동안 배터리 이슈때문에 소스트리를 꺼려온 분이 계시다면 이제 다시 한번 기회를 줘 보는 건 어떨까요.

이상입니다.