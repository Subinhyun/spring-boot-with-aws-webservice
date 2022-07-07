## Chapter 04 머스테치로 화면 구성하기

</br>

### 서버 템플릿 엔진과 머스테치

템플릿 엔진 : 지정된 템플릿 양식과 데이터가 합쳐져 HTML 문서를 출력하는 소프트웨어

서버 템플릿 엔진 - 화면 생성 : 서버에서 Java 코드로 문자열을 만든뒤 HTML로 변환하여 브라우저로 전달

자바스크립트 코드는 브라우저 위에서 작동

### 머스테치란?

머스테치 : 수많은 언어를 지원하는 가장 심플한 템플릿 엔진

자바 진영에서의 템플릿 엔진 

- JSP, Velocity : 스프링 부트에서는 권장하지 않는 템플릿 엔진
- Freemarker : 템플릿 엔진으로는 너무 과하게 많은 기능을 지원. 높은 자유도로 인해 숙련도가 낮을 수록 Freemarker 안에 비즈니스 로직이 추가될 확률이 높음
- Thymeleaf : 스프링 진영에서 적극적으로 밀고 있지만 문법이 어려움(HTML 태그에 속성으로 템플릿 기능을 사용하는 방식)

#### 장점
- 문법이 다른 템플릿 엔진보다 심플
- 로직 코드를 사용할 수 없어 View의 역할과 서버의 역할이 명확하게 분리
- Mustache.js와 Mustache.java 2가지가 있어 하나의 문법으로 클라이언트/서버 템플릿 모두 사용ㄷ 가능
- 인텔리제이 커뮤니티 버전에도 무료로 지원(Handlebars/Mustache... 플러그인 설치)

</br>

---

### 기본 페이지 만들기

머스테치 플러그인 설치 후 의존성 추가 

```
implementation 'org.springframework.boot:spring-boot-starter-mustache'
```

resources > templates > index.mustache 추가

```html
  <!DOCTYPE HTML>
  <html>
  <head>
  	<title>스프링부트로 시작하는 웹 서비스><title>
  	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
  	<h1>스부링부트로 시작하는 웹 서비스 Ver.2</h1>
  </body>
  </html>
```

IndexController 추가


```java
@Controller
public class IndexController {
    @GetMapping("/")
    public String index() {
        return "index";
    }
}
```

IndexControllerTest 추가

```java
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class IndexControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void mainPage_Loading() {
        String body = this.restTemplate.getForObject("/", String.class);
        assertThat(body).contains("스프링 부트로 시작하는 웹 서비스");
    }
}
```