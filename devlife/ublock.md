# Chrome uBlock Origin 막힘 해결

## Intro

> **2025.09.03 추가**
>
> Chrome 139 버전까지는 설정 바꿔가면서 어떻게든 썼지만 140부터는 설정변경으로도 활성화가 안된다.
>
> 이제는 어쩔 수 없이 크롬에서는 Ublock Origin Lite를 사용해야 한다.
>
> https://chromewebstore.google.com/detail/ublock-origin-lite/ddkjiahejlhfcafbddmgiahcphecmpfh
>
> 다만 강력했던 이전의 광고차단 기능이 다소 꺾였으니, 필요한 상황에서는 Firefox 등을 대안으로 사용해야 한다.

...

Chrome 에서 uBlock을 완전 멈춰세웠다.

> This extension is no longer available because it doesn't follow best practices for Chrome extensions

이라고 뜨며 2025년 3월부터 못쓰게 하긴 했었지만, 그래도 크롬에서 제거하라는 제안을 무시하고 Extension 목록에서 사용함으로 체크하면 계속 쓸 수는 있었는데 이번에는 그렇게도 사용 못하게 해버렸다. 사용 체크가 안된다.

그래도 아직은 사용을 유지하도록 하는 방법이 있다.

## 해결

아래의 텍스트를 Chrome 주소창에 치고 설정에 들어가서 사용함으로 변경한다.

```
chrome://flags/#temporary-unexpire-flags-m137                     [Enabled]
```

이후 크롬을 재 시작하고 아래의 옵션들도 각각 설정해준다.

```
chrome://flags/#extension-manifest-v2-deprecation-warning         [Disabled]
chrome://flags/#extension-manifest-v2-deprecation-disabled        [Disabled]
chrome://flags/#extension-manifest-v2-deprecation-unsupported     [Disabled]
chrome://flags/#allow-legacy-mv2-extensions                       [Enabled]
```

잘 이해가 안된다면 아래의 동영상 자료를 참고하면 된다.

https://www.reddit.com/r/uBlockOrigin/comments/1lx59m0/restoring_access_to_ubo_on_chrome_138_using_flags/

끝

**References**

- https://www.reddit.com/r/uBlockOrigin/comments/1lwztf1/ublockorigin_fully_disabled_on_chrome_now/