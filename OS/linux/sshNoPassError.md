# SSH 비밀번호 없이 공개키 로그인이 안될때

## 증상

ssh 접속시 비밀번호를 입력하고 로그인 하지 않기 위해 `authorized_keys`에 공개키를 추가 했지만, 여전히 비밀번호를 물어 보는 문제가 있었습니다. ssh 공개키도 제대로 입력 했는데 왜 비밀번호를 물어보는지 문제를 해결 할 필요가 있습니다.

## 원인

원인을 파악하기 위해 secure 파일을 확인 해 봅니다.

```bash
sudo cat /var/log/secure
```

<br><br>

로그를 확인 하니 아래와 같습니다.

```bash
[dev@serverpc .ssh]$ sudo cat /var/log/secure
Nov 18 11:09:53 serverpc sshd[4851]: Accepted password for dev from 127.0.0.1 port 9999 ssh2
Nov 18 11:09:53 serverpc sshd[4851]: pam_unix(sshd:session): session opened for user dev by (uid=0)
Nov 18 11:09:56 serverpc sshd[4855]: Received disconnect from 127.0.0.1 port 9999:11: disconnected by user
Nov 18 11:09:56 serverpc sshd[4855]: Disconnected from 127.0.0.1 port 9999
Nov 18 11:09:56 serverpc sshd[4851]: pam_unix(sshd:session): session closed for user dev
Nov 18 11:11:18 serverpc sshd[4875]: Accepted password for dev from 192.168.0.10 port 43000 ssh2
Nov 18 11:11:18 serverpc sshd[4875]: pam_unix(sshd:session): session opened for user dev by (uid=0)
Nov 18 11:14:06 serverpc sshd[4875]: pam_unix(sshd:session): session closed for user dev
Nov 18 11:14:10 serverpc sshd[4918]: Authentication refused: bad ownership or modes for directory /home/dev/.ssh
Nov 18 11:14:16 serverpc sshd[4918]: Accepted password for dev from 192.168.0.10 port 43516 ssh2
Nov 18 11:14:16 serverpc sshd[4918]: pam_unix(sshd:session): session opened for user dev by (uid=0)
Nov 18 11:14:44 serverpc sshd[4918]: pam_unix(sshd:session): session closed for user dev
Nov 18 11:14:46 serverpc sshd[4946]: Authentication refused: bad ownership or modes for directory /home/dev/.ssh
Nov 18 11:14:49 serverpc sshd[4946]: Accepted password for dev from 192.168.0.10 port 43520 ssh2
Nov 18 11:14:49 serverpc sshd[4946]: pam_unix(sshd:session): session opened for user dev by (uid=0)
Nov 18 11:18:47 serverpc sudo:   dev : TTY=pts/0 ; PWD=/home/dev/.ssh ; USER=root ; COMMAND=/bin/systemctl status ssh
Nov 18 11:18:47 serverpc sudo: pam_unix(sudo:session): session opened for user root by dev(uid=0)
Nov 18 11:18:47 serverpc sudo: pam_unix(sudo:session): session closed for user root
Nov 18 11:25:04 serverpc sudo:   dev : TTY=pts/0 ; PWD=/home/dev/.ssh ; USER=root ; COMMAND=/bin/cat /var/log/secure
Nov 18 11:25:04 serverpc sudo: pam_unix(sudo:session): session opened for user root by dev(uid=0)

```

이 중에서 `Authentication refused: bad ownership or modes for directory /home/dev/.ssh` 에 주목 해야 합니다. .ssh 폴더의 소유권이나 모드가 올바르지 않다고 합니다.

`authorized_keys` 파일의 권한 설정은 잘 해주었는데, `.ssh` 폴더의 디렉토리 권한을 변경 해 주지 않아서 생긴 문제 였습니다.

## 해결

`.ssh` 폴더의 권한을 아래와 같이 변경 해 줍니다.

```bash
chmod 700 ~/.ssh
```

```bash
chmod 644 ~/.ssh/authorized_keys
```

<br><br>

이제 ssh로 서버에 다시 접속을 해 보면 비밀번호 없이 잘 접속이 되는걸 확인 할 수 있습니다.



