## Chapter 02 스프링부트에서 테스트 코드를 작성하자 

</br>

### TDD : 테스트 주도 개발

- 항상 실패하는 테스트를 먼저 작성하고 
- 테스트가 통과하는 프로덕션 코드를 작성하고
- 테스트가 통과하면 프로덕션 코드를 리팩토링한다.

### 단위 테스트 : 기능 단위의 테스트 코드를 작성하는 것

</br>

---

</br>

Application 클래스 : 메인 클래스

@SpringBootApplication이 있는 위치 부터 설정을 읽음 - 프로젝트 최상단에 위치

내장 AWS 사용 권장 - '언제 어디서나 같은 환경에서 스프링 부트를 배포'

</br>

@RestController 
- 컨트롤러를 JSON을 반환하는 컨트롤러로 만들어줌

@GetMapping
- HTTP Method인 Get의 요청을 받을 수 있는 API를 만들어줌

</br>

```
@RestController
public class HelloController {
    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }
}
```

</br>

---

</br>

@RunWith(SpringRunner.class)
- 테스트를 진행할 때 JUnit에 내장된 실행자 외에 다른 실행자(SpringRunner.class)를 실행시킴
- 스프링 부트 테스트와 JUnit 사이에 연결자 역할

@WebMvcTest
- Web(Spring Mvc)에 집중할 수 있는 어노테이션
- 선언할 경우 @Controller, @ControllerAdvice 등을 사용 가능
- @Service, @Component, @Repository 등은 사용 불가

@Autowired
- 스프링이 관리하는 빈(Bean)을 주입 받음

@private MockMvc mvc
- 웹 API를 테스트할 때 사용
- 스프링 Mvc 테스트의 시작점
- 이 클래스를 통해 HTTP GET, POST 등에 대한 API 테스트 가능

mvc.perform(get("/hello"))
- MockMvc를 통해 /hello 주소로 HTTP GET 요청
- 체이닝 지원, 여러 검증 기능을 이어서 선언 가능

.andExpect(status().isOk())
- mvc.perform의 결과를 검증
- HTTP Header의 Status를 검증
- 200, 404, 500 등의 상태를 검증
- 여기선 OK - 200인지 아닌지 검증

.andExpect(content().string(hello))
- mvc.perform의 결과를 검증
- 응답 본문에 내용을 검증
- Controller에서 "hello"를 리턴하기 때문에 이 값이 맞는지 검증

</br>

```
@RunWith(SpringRunner.class)
@WebMvcTest(controllers = HelloController.class)
public class HelloControllerTest {
    @Autowired
    private MockMvc mvc;

    @Test
    public void helloReturn() throws Exception{
        String hello = "hello";

        mvc.perform(get("/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string(hello));
    }
}
```

</br>

---

</br>

! error
```
No tests found for given includes: [com.hsooovn.springbootwebservice.HelloControllerTest](--tests filter)
```

<img width="978" alt="스크린샷 2022-06-28 오후 12 27 48" src="https://user-images.githubusercontent.com/48265714/176086213-ed79972a-cbd0-4b69-8027-c21e4a9b1c63.png">

Build > Gradle > Run tests using > IntelliJ IDEA로 변경

</br>

---

</br>

Test 코드와 Application 파일도 실행해서 정상적으로 값이 출력되는지 확인

localhost:8080/hello로 접속해서 브라우저 결과 확인

<img width="710" alt="스크린샷 2022-06-28 오후 12 37 45" src="https://user-images.githubusercontent.com/48265714/176086869-8c0b3716-c585-480d-ae80-52c4ecc684e1.png">

</br>

---

</br>

## 롬복 설치

build.gradle에 추가

```
compileOnly 'org.projectlombok:lombok'
annotationProcessor 'org.projectlombok:lombok'
```

Hello Controller 코드를 롬복으로 전환

@Getter 
- 선언된 모든 필드의 get 메소드를 생성

@RequiredArgsConstructor
- 선언된 모든 final 필드가 포함된 생성자를 생성해줌
- final이 없는 필드는 생성자에 포함되지 않음


```
@Getter
@RequiredArgsConstructor
public class HelloResponseDto {
    private final String name;
    private final int amount;
}
```

</br>

---

</br>

assertThat
- assertj라는 테스트 검증 라이브러리의 검증 메소드
- 검증하고 싶은 대상을 메소드 인자로 받음
- 메소드 체이닝이 지원되어 isEqualTo와 같이 메소드를 이어서 사용 가능

isEqualTo
- assertj의 동등 비교 메소드
- assertThat에 있는 값과 isEqualTo의 값을 비교해서 같을 때만 성공

```
public class HelloResponseDtoTest {
    @Test
    public void LombokTest() {
        String name = "Test";
        int amount = 1000;

        HelloResponseDto dto = new HelloResponseDto(name, amount);
        assertThat(dto.getName()).isEqualTo(name);
        assertThat(dto.getAmount()).isEqualTo(amount);
    }
}
```

assertj의 장점
- CoreMatchers와 달리 추가적으로 라이브러리가 필요하지 않음
- 자동완성이 좀 더 확실하게 지원됨

</br>

---

</br>

@RequestParam
- 외부에서 API로 넘긴 파라미터를 가져오는 어노테이션

```
@GetMapping("/hello/dto")
public HelloResponseDto helloResponseDto(
        @RequestParam("name") String name,
        @RequestParam("amount") int amount)
{
    return new HelloResponseDto(name, amount);
}
```

</br>

---

</br>

param
- API 테스트할 때 사용될 요청 파라미터를 설정(값은 String만 허용)
- 숫자/날짜 등의 데이터도 등록할 때는 문자열로 변경해야만 가능

jsonPath
- JSON 응답값을 필드별로 검증할 수 있는 메소드
- $를 기준으로 필드명 명시


```
@Test
public void helloDtoReturn() throws Exception{
    String name = "hello";
    int amount = 1000;

    mvc.perform(
            get("/hello/dto")
                    .param("name", name)
                    .param("amount", String.valueOf(amount)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is(name)))
            .andExpect(jsonPath("$.amount", is(amount)));
}
```