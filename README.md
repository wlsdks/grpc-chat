# grpc-example
SpringBoot3.x.x 이상 버전의 grpc 프로젝트 예시입니다.

# grpc 채팅
### grpc를 이용한 채팅 프로젝트입니다.

<br/>

### 구성
- grpc-server : grpc 서버
- grpc-client : grpc 클라이언트

<br/>

### 최초 접속
- http://localhost:8090/chat/{roomId}?user={user} 에 접속하면 chat.mustache 템플릿을 렌더링합니다.

<br/>

### 여러명이 같은 roomId를 입력하고 뒤에 user를 다르게 설정해서 채팅하면 대화가 가능합니다.
- http://localhost:8090/chat/test?user=test
- http://localhost:8090/chat/test?user=what

이렇게 접속하면 test라는 유저와 what이라는 유저가 test라는 채팅방에 접속해서 대화를 할 수 있습니다.