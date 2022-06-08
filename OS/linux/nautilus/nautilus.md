# Nautilus 필요없는 바로가기 제거

> 좋지 않은 방법이라고 생각해 블로그에 올리지는 않고 백업용으로 저장.

![image-20211126094836386](/home/shane/Documents/git/mdblog/OS/linux/nautilus/nautilus.assets/image-20211126094836386.png)

Nautilus 에서는 기본적으로 Desktop, Documents, Downloads, Music, Pictures, Videos 등의 경로가 Bookmark 되어 있는데요.

사용하지도 않는 폴더들이 떡하니 있으니 굉장히 거슬립니다.

![image-20211126094945464](/home/shane/Documents/git/mdblog/OS/linux/nautilus/nautilus.assets/image-20211126094945464.png)

> 심지어 기본으로 등록된 폴더들은 Remove가 먹히지도 않습니다.

#### Videos 와 Music 제거

저는 Videos와 Music은 전혀 사용할 일이 없기 때문에 차라리 그 공간에 다른 Bookmark를 더 추가하기 위해 삭제 하려고 합니다.

해당 설정은 `.config` 폴더에 있는 `user-dirs.dirs` 에 있습니다.

```bash
sudo vi ~/.config/user-dirs.dirs
```

![image-20211126095224026](/home/shane/Documents/git/mdblog/OS/linux/nautilus/nautilus.assets/image-20211126095224026.png)

사용 하지 않을 폴더 앞에 `#` 을 붙여 주석 처리 하면 되겠습니다. 저는 Music과 Video를 없애 보겠습니다.

```bash
XDG_DESKTOP_DIR="$HOME/Desktop"
XDG_DOWNLOAD_DIR="$HOME/Downloads"
XDG_TEMPLATES_DIR="$HOME/Templates"
XDG_PUBLICSHARE_DIR="$HOME/Public"
XDG_DOCUMENTS_DIR="$HOME/Documents"
# XDG_MUSIC_DIR="$HOME/Music"
XDG_PICTURES_DIR="$HOME/Pictures"
# XDG_VIDEOS_DIR="$HOME/Videos"
```

이제 nautilus를 재시작 해야 하는데요, 로그아웃 하기는 귀찮으니 아래의 명령어로 종료 시킵니다.

```bash
nautilus -q
```

이제 nautilus를 다시 켜 보면

![image-20211126095636737](/home/shane/Documents/git/mdblog/OS/linux/nautilus/nautilus.assets/image-20211126095636737.png)

원래 삭제 되지 않던 Music과 Videos가 아래로 내려와 있습니다.

![image-20211126095723295](/home/shane/Documents/git/mdblog/OS/linux/nautilus/nautilus.assets/image-20211126095723295.png)

이제는 Remove가 가능합니다. 삭제 하겠습니다.

![image-20211126095940599](/home/shane/Documents/git/mdblog/OS/linux/nautilus/nautilus.assets/image-20211126095940599.png)

> 삭제 후 조금 더 깔끔해 졌습니다.

하지만 해당 방법은 컴퓨터를 껐다 켜면 다시 원상 복귀가 되는데, 그 이유는 `/etc/xdg/user-dirs.defaults` 에서 rebuilt 되기 때문.

해당 파일에서도 MUSIC과 VIDEOS를 주석 처리 해준다.

 ```bash
 sudo vi /etc/xdg/user-dirs.defaults
 ```

![image-20211126111236113](/home/shane/Documents/git/mdblog/OS/linux/nautilus/nautilus.assets/image-20211126111236113.png)

이렇게 까지 하면 재시작 하더라도 다시 살아나지 않는다.

#### Recent 제거

맨 위의 Recent 탭은 Preferences > Privacy > File History & Trash 에서 File History 를 끄면 없어집니다.

![image-20211126100659757](/home/shane/Documents/git/mdblog/OS/linux/nautilus/nautilus.assets/image-20211126100659757.png)

설정 변경 후에는 Nautilus를 새로 켜줍니다.

```bash
nautilus -q
```

![image-20211126100733196](/home/shane/Documents/git/mdblog/OS/linux/nautilus/nautilus.assets/image-20211126100733196.png)

#### Starred 제거

마지막으로 Starred 만 없애면 원하는 대로 되겠는데요, 이게 가장 까다롭습니다. sidebar는 편집 가능한 컬렉션 아이템이 아닌 Gtk가 제공하는 하나의 싱글 유닛이기 때문인데요. 환경설정을 오버라이딩 하는 방식으로 해결 할 수 있습니다만 추천하지 않습니다.

대충 아래의 단계로 진행 되지만 추천하지 않습니다.

```bash
mkdir ~/.config/nautilus/ui
```

```bash
gresource extract /bin/nautilus \
          /org/gnome/nautilus/ui/nautilus-window.ui \
          > ~/.config/nautilus/ui/nautilus-window.ui
```

`nautilus-window.ui` 파일의 24번 라인에 해당 내용이 있습니다. False로 변경 해 줍니다.

- 환경 설정 파일을 방금 작성한 파일로 overide 

```bash
export G_RESOURCE_OVERLAYS="/org/gnome/nautilus/ui=$HOME/.config/nautilus/ui"
```

만약 Starred 제거를 시도 했다가 너무 꼬인것 같으면 아래의 순서로 되돌립니다.

- 환경변수삭제

```bash
unset G_RESOURCE_OVERLAYS
```

- nautilus가 꼬였다면 재설치

```bash
sudo apt remove nautilus
sudo apt install nautilus
```
