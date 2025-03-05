# Selenium과 2Captcha로 Cloudflare Turnstile 우회

## Intro

웹 스크래핑을 하다 보면 Cloudflare Turnstile 캡차가 가로막는 경우가 많다. 특히, 자동화된 요청을 차단하려는 사이트에서는 이걸 우회하지 않으면 데이터를 가져올 수 없다. API가 제공되지 않는 경우, 어쩔 수 없이 크롤링을 통해 데이터를 수집해야 하지만, Turnstile이 이를 방해할 수 있다.

![2](https://raw.githubusercontent.com/ShanePark/mdblog/main/development/2captcha-turnstile.assets/2.webp)

> Verify you are human by completing the action below.

위와 같이 Turnstile이 앞을 가로막아 자동수집이 안되면 곤란하다.

이번 글에서는 Selenium과 2Captcha API를 활용해 Cloudflare 캡차를 뚫고 웹페이지에 자동으로 접속하는 과정을 정리한다. 또한, 성공적으로 접속한 후 HTML을 저장하는 방법까지 다룬다. 이제부터 캡차를 풀고 페이지를 저장하는 코드를 단계별로 살펴보자.

아무래도 Captcha 는 창과 방패의 대결이기 때문에 주기적으로 코드를 갱신야 한다. 그래도 참고한 예제 코드가 6개월 전에 업데이트된걸 봐서는 엄청 번거롭게 하지는 않는 모양이다.

## API Key 준비

> https://2captcha.com

위의 사이트에 가입해준다. 

![7](https://raw.githubusercontent.com/ShanePark/mdblog/main/development/2captcha-turnstile.assets/7.webp)

사이트 대문에는 자랑스럽게 실시간으로 풀어지고 있는 captcha 들의 현황과 각 타입별 비용이 적혀있다. Cloudflare Turnstile은 1,000 건당 `$1.45` 으로, 대략 한번 풀때마다 2원씩 든다고 보면 된다. 굉장히 저렴한 편이라 생각된다.

회원 가입 후에는 Worker 또는 Developer로 가입하는지를 체크하라고 하는데, 둘 다 할 수 있기 때문에 크게 신경 쓰지 않아도 된다.

![3](https://raw.githubusercontent.com/ShanePark/mdblog/main/development/2captcha-turnstile.assets/3.webp)

> Worker Dashboard

![4](https://raw.githubusercontent.com/ShanePark/mdblog/main/development/2captcha-turnstile.assets/4.webp)

> Developer dashboard

이제 API 사용을 위해서는 약간의 충전을 해야하는데, 재밌는건 Worker로도 등록할 수 있기 때문에 내가 필요한 요금을 스스로 벌어서 사용할 수 있다는거다. 아래의 예시는 비슷한 기능을 제공하는 다른 사이트인데, 인도, 파키스탄, 필리핀, 베트남 등의 실제 사람들이 캡챠를 풀어준다고 홍보하고 있다.

![1](https://raw.githubusercontent.com/ShanePark/mdblog/main/development/2captcha-turnstile.assets/1.webp)

> https://anti-captcha.com/
> 이 도표를 처음 봤을때는 스스로가 저 도넛 안에 들어가게 될거란 생각을 못했다.

자랑스럽게 Worker로 활동하여 약간의 한국인 지분을 늘리고 직접 벌어 사용하거나, 그게 싫다면 약간의 결제를 해야한다.

최소 결제금액을 확인하니 `$3` ...  본인은 괴롭지만 스스로 벌어서 채워넣는걸 선택했다.

Worker Dashboard 에서 `Start work`를 누르면 아주 간단하게 일을 시작할 수 있다. 

![8](https://raw.githubusercontent.com/ShanePark/mdblog/main/development/2captcha-turnstile.assets/8.webp)

> 이런 문제를 열심히 대신 풀어주면 된다.
>
> API가 저렴하다고 좋아했는데 문제풀이할 때 버는 돈도 저렴하다. 위의 rate 의 경우에는 비슷한 문제를 무려 15번 제출해야 Cloudfare Turnstile 을 1회 풀 수 있는 비용이 준비된다. 다행히 어려운 문제들은 Rate 가 비교적 높다. 
>
> 열심히 풀고 나왔는데 Finances가 여전히 $0.00 이라고 표기된다고 너무 슬퍼 말자. $0.01 아래로 모았기 때문인데 사라진게 아니고 너무 적어서 안보이는 것 뿐이다.

참고로 응답율과 정확도라는 두가지 지표를 측정하는데, 둘 다 95% 이상을 유지해야 한다. 문제가 알아보기 힘들다고 skip 해버리면 안되고 진짜로 절대 풀 수 없는 상황에서만 `Cannot solve`를 선택해야 한다. 개인적으로 `CAPTCHA` 상황에서 어려운건 다음걸로 넘기는 걸 선호하는 편이라서 평소대로 알아보기 힘든 문제를 그냥 넘겼다가 Solved captchas 가 뚝 떨어지는 문제가 있었다.

![5](https://raw.githubusercontent.com/ShanePark/mdblog/main/development/2captcha-turnstile.assets/5.webp)

열심히 문제를 풀었거나 돈을 지불 했다면 어느정도 Finances가 쌓였을 텐데 이제 Developer Dashboard 에서 하단의 API Key를 복사해두면 준비가 끝난다. 참고로 Worker 와 Developer 의 API Key가 별개로 존재하니 반드시 Developer의 key를 준비하자.

![6](https://raw.githubusercontent.com/ShanePark/mdblog/main/development/2captcha-turnstile.assets/6.webp)

## 개발

### 필요 라이브러리 설치

먼저 Python 환경에서 필요한 패키지를 설치한다.

```bash
pip install selenium 2captcha-python
```

- `selenium` : 브라우저 자동화를 위한 라이브러리
- `2captcha-python` : 2Captcha API를 통해 캡차를 풀기 위한 라이브러리. `twocaptcha` 같은 비공식 말고 반드시 `2captcha-python` 를 설치하자.

> Selenium을 사용하려면 Chrome WebDriver도 필요하다. Chrome 버전에 맞는 최신의 WebDriver를 설치하고, 환경 변수에 추가하자.

### 2Captcha API 키 설정

1. 위에서 가입해 준비한 API Key 확인 후, `config.json` 파일을 생성해 저장한다.

```json
{
    "2captcha_api_key": "여기에_본인의_2captcha_API_키를_입력"
}
```

이제 Python 코드에서 API 키를 불러올 수 있다.

### 전체 코드

먼저 실행 결과 및 전체 예제 코드를 보여주고 나서 단계별로 설명하도록 하겠다.

![9](https://raw.githubusercontent.com/ShanePark/mdblog/main/development/2captcha-turnstile.assets/9.webp)

> 예제 코드에서는 실행 한 폴더에 `output.html` 로 결과를 저장하게 하였는데, captcha 풀이 이후에는 다른 링크를 방문하거나 파싱을 하거나 각자 필요한 일을 하면 된다.

```python
import os
import time
import json
import re
from selenium import webdriver
from selenium.webdriver.support.wait import WebDriverWait
from selenium.webdriver.common.by import By
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.chrome.options import Options
from twocaptcha import TwoCaptcha

# CONFIGURATION
with open("config.json", "r") as config_file:
    config = json.load(config_file)

apikey = config["2captcha_api_key"]
url = "https://2captcha.com/demo/cloudflare-turnstile-challenge"

intercept_script = """ 
    console.clear = () => console.log('Console was cleared')
    const i = setInterval(()=>{
    if (window.turnstile)
     console.log('success!!')
     {clearInterval(i)
         window.turnstile.render = (a,b) => {
          let params = {
                sitekey: b.sitekey,
                pageurl: window.location.href,
                data: b.cData,
                pagedata: b.chlPageData,
                action: b.action,
                userAgent: navigator.userAgent,
            }
            console.log('intercepted-params:' + JSON.stringify(params))
            window.cfCallback = b.callback
            return        
         } 
    }
},50)    
"""

# ACTIONS
def get_captcha_params(script):
    """
    Refreshes the page, injects a JavaScript script to intercept Turnstile parameters, and retrieves them.

    Returns:
        dict: The intercepted Turnstile parameters as a dictionary.
    """
    print("[INFO] 페이지를 새로고침하여 캡차 인터셉트 시도 중...")
    browser.refresh() 

    print("[INFO] 캡차 인터셉트 스크립트 실행 중...")
    browser.execute_script(script)

    time.sleep(5)

    logs = browser.get_log("browser")
    params = None
    for log in logs:
        if "intercepted-params:" in log['message']:
            log_entry = log['message'].encode('utf-8').decode('unicode_escape')
            match = re.search(r'intercepted-params:({.*?})', log_entry)
            if match:
                json_string = match.group(1)
                params = json.loads(json_string)
                break
    if params:
        print("[SUCCESS] 캡차 파라미터 인터셉트 성공!")
    else:
        print("[ERROR] 캡차 파라미터를 찾을 수 없습니다.")
    return params

def solver_captcha(apikey, params):
    """
    Solves the Turnstile captcha using the 2Captcha service.

    Returns:
        str: The solved captcha token.
    """
    print("[INFO] 2Captcha를 이용하여 캡차 풀이 중...")
    solver = TwoCaptcha(apikey)
    try:
        result = solver.turnstile(sitekey=params["sitekey"],
                                  url=params["pageurl"],
                                  action=params["action"],
                                  data=params["data"],
                                  pagedata=params["pagedata"],
                                  useragent=params["userAgent"])
        print("[SUCCESS] 캡차 풀이 완료!")
        return result['code']
    except Exception as e:
        print(f"[ERROR] 캡차 풀이 중 오류 발생: {e}")
        return None

def send_token_callback(token):
    """
    Executes the callback function with the given token.
    """
    print("[INFO] 캡차 토큰을 브라우저에 전달 중...")
    script = f"cfCallback('{token}')"
    browser.execute_script(script)
    print("[SUCCESS] 캡차 토큰이 성공적으로 전달됨.")

def save_page_html():
    """
    Saves the current page's HTML to a file and prints the absolute path.
    """
    file_path = os.path.abspath("output.html")  # 절대 경로 얻기
    print(f"[INFO] 페이지 HTML을 {file_path} 에 저장 중...")

    html_content = browser.page_source
    with open(file_path, "w", encoding="utf-8") as file:
        file.write(html_content)

    print(f"[SUCCESS] 페이지 HTML 저장 완료! 저장 위치: {file_path}")

# LOCATORS
locator = "//p[contains(@class,'successMessage')]"

def check_success():
    """
    Checks if the element exists on the page.
    If found, saves the page HTML.
    """
    try:
        print("[INFO] 페이지 성공 여부 확인 중...")
        WebDriverWait(browser, 10).until(EC.presence_of_element_located((By.XPATH, locator)))
        print("[SUCCESS] 페이지가 정상적으로 로드됨! <div class='successMessage'> 발견됨.")
        save_page_html()
    except Exception:
        print("[WARNING] <div class='successMessage'> 요소를 찾을 수 없음.")

# MAIN LOGIC
chrome_options = Options()
chrome_options.add_argument("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) "
                            "Chrome/126.0.0.0 Safari/537.36")
chrome_options.set_capability("goog:loggingPrefs", {"browser": "INFO"})

with webdriver.Chrome(service=Service(), options=chrome_options) as browser:
    print("[INFO] 브라우저 실행 중...")
    browser.get(url)
    print("[INFO] 웹사이트 접속 완료.")

    params = get_captcha_params(intercept_script)

    if params:
        token = solver_captcha(apikey, params)

        if token:
            send_token_callback(token)
            time.sleep(5)  # 페이지가 로드될 시간을 줌

            check_success()

            print("[SUCCESS] 모든 과정 완료!")
        else:
            print("[ERROR] 캡차 풀이 실패.")
    else:
        print("[ERROR] 캡차 인터셉트 실패.")

```

전체 코드를 확인했으니 단계별로 잘라서 알아보자.

### Selenium으로 웹사이트 접속

Selenium을 이용해 웹사이트에 접속하는 코드이다.

```python
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.chrome.options import Options

# Chrome 옵션 설정
chrome_options = Options()
chrome_options.add_argument("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36")

# Chrome WebDriver 실행
browser = webdriver.Chrome(service=Service(), options=chrome_options)

# 접속할 웹사이트
url = "Turnstile 이 적용된 웹사이트 주소"
browser.get(url)
print("[INFO] 웹사이트 접속 완료.")
```

### Cloudflare Turnstile 캡차 인터셉트

웹사이트에 접속한 후, 캡차를 우회하려면 Turnstile에서 사용하는 파라미터를 가로채야 한다.

Selenium에서 JavaScript를 실행하여 `sitekey`, `pageurl`, `data` 등의 정보를 가져오도록 설정한다.

```python
intercept_script = """ 
    console.clear = () => console.log('Console was cleared')
    const i = setInterval(()=>{
    if (window.turnstile)
     console.log('success!!')
     {clearInterval(i)
         window.turnstile.render = (a,b) => {
          let params = {
                sitekey: b.sitekey,
                pageurl: window.location.href,
                data: b.cData,
                pagedata: b.chlPageData,
                action: b.action,
                userAgent: navigator.userAgent,
            }
            console.log('intercepted-params:' + JSON.stringify(params))
            window.cfCallback = b.callback
            return        
         } 
    }
},50)    
"""

# 인터셉트 스크립트 실행
browser.execute_script(intercept_script)
print("[INFO] 캡차 인터셉트 스크립트 실행 중...")
```

이제 Turnstile 캡차에서 사용하는 주요 파라미터를 가로챌 수 있다.

### 2Captcha API로 캡차 풀이

캡차를 풀기 위해 2Captcha API에 요청을 보내고, 해결된 토큰을 받아온다. 위에서 인터셉터한 파라미터들을 보낸다.

```python
from twocaptcha import TwoCaptcha
import json

# API 키 로드
with open("config.json", "r") as config_file:
    config = json.load(config_file)

apikey = config["2captcha_api_key"]
solver = TwoCaptcha(apikey)

# 캡차 파라미터 받아오기
params = {
    "sitekey": "가로챈_sitekey",
    "pageurl": "가로챈_pageurl",
    "data": "가로챈_data",
    "pagedata": "가로챈_pagedata",
    "action": "가로챈_action",
    "userAgent": "가로챈_userAgent"
}

# 2Captcha API 호출
print("[INFO] 2Captcha를 이용하여 캡차 풀이 중...")
result = solver.turnstile(params)
captcha_token = result['code']
print(f"[SUCCESS] 캡차 풀이 완료! 토큰: {captcha_token}")
```

이제 캡차 토큰을 얻었으므로, 웹사이트에 적용하면 된다.

### 해결된 캡차 토큰 적용

브라우저에서 해결된 캡차 토큰을 적용하여 인증을 완료한다.

```python
# 캡차 토큰을 사이트에 전달
script = f"cfCallback('{captcha_token}')"
browser.execute_script(script)
print("[SUCCESS] 캡차 토큰이 성공적으로 전달됨.")
```

이제 Cloudflare Turnstile이 해결되고, 정상적으로 웹사이트를 이용할 수 있다.

### 성공적인 접속 확인 및 HTML 저장

캡차를 푼 후, 정상적으로 페이지에 접속했는지 확인하고, HTML을 저장한다.

```python
import os
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.by import By

def save_page_html():
    """
    Saves the current page's HTML to a file.
    """
    file_path = os.path.abspath("output.html")
    print(f"[INFO] 페이지 HTML을 {file_path} 에 저장 중...")

    html_content = browser.page_source
    with open(file_path, "w", encoding="utf-8") as file:
        file.write(html_content)

    print(f"[SUCCESS] 페이지 HTML 저장 완료! 저장 위치: {file_path}")

# 페이지 성공 여부 확인
try:
    print("[INFO] 페이지 성공 여부 확인 중... `.container` 엘리먼트가 있는지 찾아본다.")
    WebDriverWait(browser, 10).until(EC.presence_of_element_located((By.XPATH, "//div[contains(@class,'container')]")))
    print("[SUCCESS] 페이지가 정상적으로 로드됨! <div class='container'> 발견됨.")
except:
    print("[WARNING] <div class='container'> 요소를 찾을 수 없음. 페이지를 저장합니다.")
    save_page_html()
```

> 바로 위의 코드에서는 `.container`를 성공여부 파악에 사용했는데, 방문할 웹사이트마다 다르니 개인적으로 적당한 css selector를 정해서 입력해두면 된다.

## 결론

이번 글에서는 Selenium과 2Captcha를 이용해 Cloudflare Turnstile을 우회하는 방법을 알아보았다.

### 정리하면 다음과 같다

1. Selenium으로 웹사이트 접속
2. JavaScript 실행으로 캡차 파라미터 인터셉트
3. 2Captcha API로 캡차 풀이
4. 해결된 캡차 토큰을 브라우저에 적용
5. 정상 접속 확인 및 HTML 저장

이제 Cloudflare Turnstile 에 막혀도 자동으로 해결하고 원하는 데이터를 가져올 수 있다.

**References**

- https://github.com/2captcha/captcha-solver-selenium-python-examples
- https://2captcha.com/p/cloudflare-turnstile