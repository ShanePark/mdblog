# Apache Commons net 한글 파일명 문제 해결

## 문제

다음과 같이 Apache Commons net을 이용해 FTP 접속을 하고

```java
FTPClient ftpClient = new FTPClient();
FtpConfig ftpConfig = config.getFtp();
ftpClient.connect(ftpConfig.getHost(), ftpConfig.getPort());
ftpClient.login(ftpConfig.getUsername(), ftpConfig.getPassword());
ftpClient.enterLocalPassiveMode();
ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
```

> setFileType을 해주지 않으면 다운받아온 파일이 깨진다

재귀적으로 파일 목록을 담도록 코드를 작성했다. 파일 목록을 저장해두고 다운로드는 별도로 진행한다.

```java
    private void traverse(FTPClient ftpClient, String targetPath, String curDir, List<String> targetFiles) throws IOException {
        String curFullPath = config.getCopyFrom() + separator + targetPath;
        if (StringUtils.isNotEmpty(curDir)) {
            curFullPath += separator + curDir;
        }
        FTPFile[] subFiles = ftpClient.listFiles(curFullPath);
        if (subFiles == null) {
            return;
        }
        for (FTPFile subFile : subFiles) {
            String parent = curDir;
            if (StringUtils.isNotEmpty(parent)) {
                parent += separator;
            }
            String filename = subFile.getName();
            if (filename.equals(".") || filename.equals("..")) {
                continue;
            }
            if (subFile.isDirectory()) {
                traverse(ftpClient, targetPath, parent + filename, targetFiles);
                continue;
            }
            targetFiles.add(parent + filename);
        }
    }
```

그런데 두가지 문제가 있었다

- 한글 파일명이 깨진다
- 몇몇 파일/폴더는 아에 `subFiles` 목록에도 포함되지 않고 무시된다. 단순히 한글이 포함된 여부가 중요하지 않고, 예를 들어 `다른한글.jpg`는 목록을 불러올때 잘 나오는데, `한글파일명.jpg` 는 무시되었다

## 원인

원인은 누구나 쉽게 추측할 수 있듯 당연히 인코딩 문제. 원인 파악은 어렵지 않지만 해결이 좀 골치아팠다.

## 해결

### 첫번째 시도, 파일명 변환

파일을 다운받아오는 것 까지는 문제가 안된다면 저장할 때만 제대로 된 파일명으로 저장해주면 된다.

`ftpClient.getControlEncoding()` 메서드로 현재 클라이언트의 인코딩 설정을 가져올 수 있으니

```java
String encoding = ftpClient.getControlEncoding();
String encodedFileName = new String(filename.getBytes(encoding), StandardCharsets.UTF_8);
```

파일명을 바꾸고 나서 받아온 파일을 정상적인 이름으로 저장하면 된다. 이렇게 하면 한글로된 파일명도 문제없이 그대로 저장해냈다.

하지만 `몇몇 파일/폴더는 아에 `subFiles` 목록에도 포함되지 않고 무시된다.` 라는 문제가 해결되지가 않아 이 방법은 사용할 수 없다.

### 두번째 시도, 인코딩 변환

`ftpClient.getControlEncoding()` 가 있으니 설정도 가능하겠다. `ftpClient.setControlEncoding("utf-8");`를 홏루해 UTF-8로 인코딩을 변환해주었다.

```java
FTPClient ftpClient = new FTPClient();
FtpConfig ftpConfig = config.getFtp();
ftpClient.connect(ftpConfig.getHost(), ftpConfig.getPort());
ftpClient.setControlEncoding("utf-8"); // 인코딩 설정
    
ftpClient.login(ftpConfig.getUsername(), ftpConfig.getPassword());
ftpClient.enterLocalPassiveMode();
ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
```

이렇게 해서 접속을 하니 한글파일명도 문제없이 잘 표기된다.

하지만, 2 depth 부터는 해당 경로의 파일목록을 받아올 때 비정상적으로 받아왔다. 

![image-20240110133654848](https://raw.githubusercontent.com/ShanePark/mdblog/main/devlife/todayError/2024/apache-net-ftp-encode.assets/1.webp)

위와 같은 파일 트리로 샘플을 생성해두었는데, 폴더1 하위에 `IMG_2117.JPEG` 파일만 딸랑 한개 있는것으로 subFiles를 불러오는 것이었다. 

이것도 인코딩에 관련된 문제일텐데 도무지 해결될 기미가 안보였다.

### 세번째 시도, 해결

그러다 `FTP.java`의 setControlEncoding 메서드에 작성된 javadoc을 읽어보았는데 거기에 문제 원인이 떡하니 나와있었다. 역시 다른사람이 작성한 메서드를 사용할때는 설명을 먼저 읽어보고 쓰는게 좋겠다..

![image-20240110134020333](https://raw.githubusercontent.com/ShanePark/mdblog/main/devlife/todayError/2024/apache-net-ftp-encode.assets/2.webp)

여기 나와있는대로 커넥션을 수립하기 전에 인코딩 설정을 먼저 변경해주도록 변경해보자.

```java
FTPClient ftpClient = new FTPClient();
FtpConfig ftpConfig = config.getFtp();
ftpClient.setControlEncoding("utf-8"); // 커넥션 수립 전에 인코딩 설정을 먼저 해준다.
ftpClient.connect(ftpConfig.getHost(), ftpConfig.getPort());

ftpClient.login(ftpConfig.getUsername(), ftpConfig.getPassword());
ftpClient.enterLocalPassiveMode();
ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
```

이렇게 하니 모든 인코딩 문제가 깔끔하게 해결되었다.

**References**

- https://commons.apache.org/proper/commons-net/apidocs/org/apache/commons/net/ftp/FTPClient.html
- https://commons.apache.org/proper/commons-net/apidocs/org/apache/commons/net/ftp/FTP.html