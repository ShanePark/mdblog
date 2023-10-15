# [MacOS] Karabiner 활용해 특정 텍스트 입력하는 매크로 만들기

## Intro

Windows 에는 AutoHotkey, Linux 에서는 Autokey 라는 훌륭한 자동화 유틸리티들이 있다. 

맥북 사용자 입장에서도 Apple 에서 직접 만든 `Automator`라는 훌륭한 자동화 프로그램이 있지만, 개인적으로 애플스크립트를 작성해서 해봤을때는 몇가지 단계를 거치다 보니 반응 속도가 너무 느렸다. (키 입력 후 약 300ms 후 텍스트 입력됨)

또한 기본 키보드 설정의 text replacement 기능도 있지만, 특정 키조합을 통해 입력하고 싶은 상황이라 그것도 적합하지 않았다.

참고로 애플스크립트를 이용한다면 아래와 같이 작성한 후에

```applescript
on run {input, parameters}
	tell application "System Events"
		keystroke "입력할 키 스트로크"
	end tell
end run
```

키보드 설정에 원하는 숏컷을 등록 해두면 된다. 

![image-2023101591202698 pm](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/mac/karabiner-macro.assets/1.webp)

> Automator에 등록한 스크립트의 단축키 설정

지금까지는 이렇게 사용 해 왔는데 속도가 답답해서 더 나은 방법을 모색하던 중 Karabiner의 `Complex modification key`의 응답속도가 기대보다 잘 나와서 해당 방법으로 설정했고 그 방법을 정리해 두려고 한다.

## Karabiner

### Installation

한국인 MacOS 사용자라면 `한/영` 전환 및 윈도우용 키맵에 맞춰 나온 키보드를 사용할 때 Karabiner를 활용하면 매우 편리하게 사용할 수 있기 때문에 이미 대부분이 사용하고 있을거라고 생각한다. 그래도 아직 설치하지 않았다면 brew로 설치해준다.

```shell
brew install karabiner-elements
```

### Complex Modifications

Karabiner의 기능 중 Complex Modifications 를 쓸 것이다. 아래와 같이 Complex Modificatons 메뉴에 가보면

![image-2023101592137363 pm](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/mac/karabiner-macro.assets/2.webp)

아무것도 안보이고 딸랑 `Add rule` 버튼만 있는게 보인다. Complex Modifications 는 그 이름 답게 설정법도 제법 복잡했다.

막상 Add Rule을 눌러보면 몇가지 샘플만 있고 그 외에 인터넷 에서 설정을 더 불러오기 정도가 있는데 둘 다 적합하지 않고 직접 설정을 해줘야 한다.

Mics 메뉴를 클릭하면

![image-2023101592345092 pm](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/mac/karabiner-macro.assets/3.webp)

`Export & Import` 쪽에 config folder를 여는 버튼이 있다, 저 버튼을 눌러서 폴더를 열어본다.

내 경우에는 `/Users/shane/.config/karabiner` 폴더가 열렸다. 해당 폴더에 보면 `karabiner.json` 파일이 있는데 이게 바로 우리가 수정해야 할 파일이다.

![image-2023101592513940 pm](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/mac/karabiner-macro.assets/4.webp)

> karabiner.json

파일을 열어 `complex_modifications` 가 있는 곳을 확인한다.

![image-2023101592600068 pm](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/mac/karabiner-macro.assets/5.webp)

위에 보이는 것 처럼 "rules" 쪽의 배열이 비어 있다. 거기에 아래의 예시처럼 입력해보자.

```json
{
  "description": "Cmd + Shift + 1 to type phone number",
  "manipulators": [
    {
      "from": {
        "key_code": "1",
        "modifiers": {
          "mandatory": ["command", "shift"],
          "optional": ["caps_lock"]
        }
      },
      "to": [
        {
          "key_code": "0"
        },
        {
          "key_code": "1"
        },
        {
          "key_code": "0"
        }
      ],
      "type": "basic"
    }
  ]
}
```

rules 배열 안에 입력하기 때문에, 입력 후에는 아래와 같은 모습이 된다.

![image-2023101592814984 pm](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/mac/karabiner-macro.assets/6.webp)

이제 준비가 끝났다. 

참고로 나처럼 `Karabiner Profile`을 여러개 쓰고 있는 경우에는 `Complex Modifications`에 대한 설정도 프로필의 숫자 만큼 여러개가 있기 때문에 사용중인 Profile을 잘 찾아 가서 입력해줘야 한다.

위와같이 새로운 rule을 입력 한 뒤에 다시 karabiner에서 `Complex Modifications`에 찾아가보면 아래 그림에 보이는 것 처럼 방금 입력한 설정이 추가된 것이 확인된다.

![image-2023101593029897 pm](https://raw.githubusercontent.com/ShanePark/mdblog/main/OS/mac/karabiner-macro.assets/7.webp)

이제 해당 키 입력 (Cmd + Shift + 1)을 입력해본다면 아무런 딜레이 없이 `010` 이라는 숫자가 바로 입력되는 것을 확인 할 수 있다.

## Conclusion

지금까지 테스트한 샘플을 응용해서 원하는 키 입력을 설정해서 사용하면 된다. 개인적으로 리눅스에서의 autokey 보다도 속도가 빨라 굉장히 만족스럽다. 

선착순처럼 빠른 반응속도로 미리 준비한 값을 반복 입력해야하는 상황에서는 아주 큰 도움이 될 것이다. 

**References**

- https://karabiner-elements.pqrs.org/docs/json/typical-complex-modifications-examples/