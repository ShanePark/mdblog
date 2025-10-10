HTML 파일을 국제화 하는 작업중이다.
Pebble 및 vue.js 를 쓰는 html 텍스트를 제공하면 거기에 나오는 모든 *한글 텍스트*를 국제화해서 3개의 코드블럭을 제공하라. 영어로 작성된건 절대로 국제화 하면 안되고 한글 텍스트만 한다.
요청에 제공된 prefix를 활용하여 그 prefix로 시작하게끔 한다. 

## 예시
###유저 리퀘스트
```
${prefix:modal.submission}

${html content:<button type="button" class="btn btn-default" data-dismiss="modal">아니오</button>}
```
### 응답
### HTML
```
<button type="button" class="btn btn-default" data-dismiss="modal">{{ i18n("messages", "modal.submission.cancel.decline") }}</button>
```
### messages_ko.properties
```
modal.submission.cancel.decline=아니오
```
### messages_ko.properties
```
modal.submission.cancel.decline=Decline
```

## 규칙
- 기존의 문서 형태를 절대 변경하지 말 것. 본 프롬프트의 목적은 한글로 된 텍스트를 국제화하는 것이며, 영어로 된 텍스트는 변경하거나 국제화하지 않는다. 포맷을 교정하는 것도 금지되어 있다.
- 여러 문장이 이어 번역되는 경우에도 영어로 치환해도 어색하지 않아야 한다. 단, 문장 중간에 HTML 태그나 변수가 포함된 경우, 태그와 변수는 따로 처리하고 그 전후로 필요한 한글 텍스트만 개별적으로 국제화 키를 할당한다.
아래는 그 예시이다.
```original
DMP를 제출하시면 <strong>즉시 완료 처리가 되어</strong> 공개 설정에 따라 이용자가 열람할 수 있습니다. 계속하시겠습니까?
```

```HTML
{{ i18n("messages", "modal.dmp.submit.archive.warning-1") }} <strong>{{ i18n("messages", "modal.dmp.submit.archive.warning-2") }}</strong> {{ i18n("messages", "modal.dmp.submit.archive.warning-3") }} {{ i18n("messages", "modal.dmp.submit.archive.warning-4") }}
```

```properties-en
modal.dmp.submit.archive.warning-1=When you submit the DMP,
modal.dmp.submit.archive.complete.immediately=the process will be completed immediately
modal.dmp.submit.archive.accessible.by.privacy.setting=and will be accessible to users based on the privacy settings.
modal.dmp.submit.archive.confirmation=Do you want to proceed?
```
- `common.` prefix로 시작하는 i18n 단어는 절대 임의로 만들지 않는다. 이미 존재하는 common. 키가 있는 경우만 사용하고, 없을 경우 다른 prefix를 사용한다.
- properties 목록은 중간에 빈 줄이 없도록 모두 붙여서 작성한다.
- prefix가 제공되지 않았다면, 답변을 하지 않고 prefix를 요청한다.
- 응답시 HTML은 변경한 부분만이 아닌 전체 HTML을 모두 보여준다.
