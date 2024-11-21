# Docker 이미지 빌드실패 (CentOS 7 EOL) Dockerfile 수정

## 문제 상황

최근 CentOS 7을 기반으로 한 프로젝트를 Docker 환경에서 빌드하려다가 문제가 발생했다. 빌드 과정에서 `yum` 명령어로 패키지를 설치하려고 하면 연결 오류가 발생하며 실패하는 상황이었다. 문제를 살펴보니 CentOS 7이 **EOL(End of Life)**에 도달하면서 공식 미러에서 패키지 다운로드가 더 이상 지원되지 않는 것이 원인이었다.

![1](https://raw.githubusercontent.com/ShanePark/mdblog/main/devops/docker/cent-7-eol.assets/1.webp)

> CeontOS 7 베이스가 404를 응답한다

### CentOS의 EOL

다들 알고 있는 것처럼 CentOS는 Red Hat Enterprise Linux(RHEL) 기반의 무료 운영 체제다. 

하지만 최근 몇 년 동안 CentOS 프로젝트는 큰 변화를 겪었다. 특히 CentOS 8의 EOL 선언과 함께 CentOS Stream으로의 전환이 큰 논란을 불러일으켰다. CentOS Stream은 롤링 릴리즈 형태로 RHEL의 안정적인 기반보다는 차기 버전에 가까운 개발 브랜치의 역할을 한다. 이 변화로 인해 기존 CentOS 사용자들은 Stream을 사용하는 데 부담을 느끼게 되었다.

이 상황에서 **Rocky Linux**와 **AlmaLinux** 같은 RHEL 기반의 무료 대안들이 주목받고 있다. 특히 Rocky Linux는 CentOS 공동 설립자인 Gregory Kurtzer가 시작한 프로젝트로, CentOS의 철학을 계승하며 RHEL의 안정성과 1:1 바이너리 호환성을 제공한다.

- **CentOS 7의 EOL 일정**:
  - CentOS 7은 2024년 6월 30일에 공식 지원이 종료되었다.
  - 이와 동시에 기본 CentOS 미러에서 CentOS 7의 패키지 접근이 차단되었다.

이 프로젝트는 데이터 마이그레이션 문제 등으로 인해 CentOS 7을 유지해야 했기 때문에 최신 버전으로의 업그레이드는 불가능한 상황이었다. 따라서 기존 Dockerfile을 수정하여 EOL 이후에도 CentOS 7의 패키지를 설치할 수 있는 방법을 찾기로 했다.

## 해결 과정

### 1. CentOS Vault 리포지토리로 변경

CentOS는 EOL 이후에도 기존 패키지를 보관하기 위해 **Vault Repository**를 제공한다. Vault는 정적 저장소로, EOL된 버전의 모든 패키지를 보관하지만, 더 이상 보안 패치를 제공하지 않는다. 이를 사용하면 기존 CentOS 7 환경을 유지하면서도 필요한 패키지를 설치할 수 있다.

기존 `yum` 설정 파일(`/etc/yum.repos.d/CentOS-Base.repo`)은 `mirror.centos.org`를 참조하고 있었다. 이를 `vault.centos.org`로 변경하여 Vault 저장소를 사용하도록 했다.

해당 명령어는 아래와 같다:

```bash
sed -i 's|^mirrorlist=|#mirrorlist=|g' /etc/yum.repos.d/CentOS-*.repo
sed -i 's|^#baseurl=http://mirror.centos.org|baseurl=http://vault.centos.org|g' /etc/yum.repos.d/CentOS-*.repo
```

이 명령어는 기존의 `mirrorlist` 항목을 주석 처리하고 `baseurl`을 `vault.centos.org`로 변경한다. 이후 `yum clean all`과 `yum makecache` 명령어로 캐시를 정리하고 재생성해주면 Vault 저장소에서 패키지를 정상적으로 가져올 수 있다.

### 2. EPEL(Extra Packages for Enterprise Linux) 리포지토리 설정

CentOS 7에서는 EPEL 리포지토리를 사용하는 경우가 많다. 하지만 EPEL도 CentOS와 마찬가지로 기본 경로에서 접근할 수 없게 되었고, EPEL의 아카이브 버전을 사용해야 한다.

아카이브 URL에서 최신 `epel-release` 패키지를 직접 설치하도록 했다:

```bash
rpm -Uvh https://archives.fedoraproject.org/pub/archive/epel/7/x86_64/Packages/e/epel-release-7-14.noarch.rpm
```

### 3. Dockerfile 수정 결과

위 과정을 Dockerfile에 반영했다. EOL로 인해 yum이 동작하지 않던 기존 Dockerfile은 CentOS Vault와 EPEL 아카이브를 사용하도록 변경되었다. 수정 후에는 Docker 이미지를 빌드할 때 `yum` 명령어가 정상적으로 동작했다.

수정된 Dockerfile의 주요 내용은 다음과 같다:

```dockerfile
# CentOS Vault 리포지토리를 사용하도록 yum 설정 변경 (CentOS 7 EOL)
RUN sed -i 's|^mirrorlist=|#mirrorlist=|g' /etc/yum.repos.d/CentOS-*.repo \
    && sed -i 's|^#baseurl=http://mirror.centos.org|baseurl=http://vault.centos.org|g' /etc/yum.repos.d/CentOS-*.repo

RUN yum -y install wget
RUN rpm -Uvh https://archives.fedoraproject.org/pub/archive/epel/7/x86_64/Packages/e/epel-release-7-14.noarch.rpm

# 필요한 패키지 설치
RUN yum -y install sudo postgresql
```

이렇게 변경한 이후에는 이전처럼 빌드가 잘 되었다.

## 결론

CentOS 7 EOL로 인해 기본 yum 리포지토리가 동작하지 않는 문제를 Vault Repository와 EPEL 아카이브를 사용하여 해결했다. 이 접근법은 레거시 프로젝트에서 CentOS 7을 유지해야 할 때 유용하지만, 다음과 같은 점을 염두에 두어야 한다:

1. **보안 취약성**:
   Vault와 아카이브는 더 이상 보안 패치를 제공하지 않는다. 따라서 장기적인 관점에서는 최신 운영 체제로의 마이그레이션이 필요하다.

2. **최신 운영 체제로 업그레이드 검토**:
   CentOS 7의 대안으로 Rocky Linux, 또는 최신 CentOS Stream으로 이전을 고려해보는 것이 좋다.
