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

</br>

---

### 게시글 등록 화면 만들기

레이아웃 방식 : 공통 영역을 별도의 파일로 분히라여 필요한 곳에서 가져다 쓰는 방식

templates > layout > footer.mustache, header.mustache 

```html 
<!DOCTYPE HTML>
<html>
<head>
    <title>스프링부트 웹서비스</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
</head>
<body>
```

```html
<script src="https://code.jquery.com/jquery-3.3.1.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"></script>

</body>
</html>
```

페이지 로딩 속도를 높이기 위해 css는 header에 js는 footer에 둠

head가 실행되고 body가 실행

</br>

index.mustache

```html 
{{>layout/header}}
  
<h1>스프링부트로 시작하는 웹 서비스 Ver.2</h1>
  
{{>layout/footer}}
```

{{>layout/header}}

- {{> }}는 현재 머스테치 파일(index.mustache)을 기준으로 다른 파일을 가져옴

</br>

index.mustache에 글 등록 버튼추가

index.mustache

```html
{{>layout/header}}
  
  	<h1>스프링부트로 시작하는 웹 서비스 Ver.2</h1>
  	<div class="col-md-12">
  		<div class="row">
  			<div class="col-md-6">
  				<a herf="/posts/save" role="button" class="btn btn-primary">글 등록</a>
  			</div>
  		</div>
  	</div>
  {{>layout/footer}}
```
< a > 태그를 이용해 글 등록 페이지로 이동하는 글 등록 버튼을 생성

 이동할 페이지의 주소는 /posts/save

이 주소에 해당하는 컨트롤러를 생성, 페이지에 관련된 컨트롤러는 모두 IndexController를 사용

</br>

indexController

```java 
@GetMapping("/posts/save")
    public String postsSave() {
        return "posts-save";
}
```

/posts/save를 호출하면 posts-save.mustache를 호출 

</br>
posts-save.mustache 추가 

```html
{{>layout/header}}

<h1>게시글 등록</h1>

<div class="col-md-12">
    <div class="col-md-4">
        <form>
            <div class="form-group">
                <label for="title">제목</label>
                <input type="text" class="form-control" id="title" placeholder="제목을 입력하세요">
            </div>
            <div class="form-group">
                <label for="author"> 작성자 </label>
                <input type="text" class="form-control" id="author" placeholder="작성자를 입력하세요">
            </div>
            <div class="form-group">
                <label for="content"> 내용 </label>
                <textarea class="form-control" id="content" placeholder="내용을 입력하세요"></textarea>
            </div>
        </form>
        <a href="/" role="button" class="btn btn-secondary">취소</a>
        <button type="button" class="btn btn-primary" id="btn-save">등록</button>
    </div>
</div>

{{>layout/footer}}
```

<img width="526" alt="스크린샷 2022-07-08 오후 3 06 33" src="https://user-images.githubusercontent.com/48265714/177927561-b90ef724-28a7-47f3-a5d7-198d0c72daaa.png">

</br>

---

### 등록 버튼 기능 추가

resource > static > js > app > index.js

```js
var main = {
    init : function () {
        var _this = this;
        $('#btn-save').on('click', function () {
            _this.save();
        });
    },
    save : function () {
        var data = {
            title: $('#title').val(),
            author: $('#author').val(),
            content: $('#content').val()
        };

        $.ajax({
            type: 'POST',
            url: '/api/v1/posts',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(data)
        }).done(function() {
            alert('글이 등록되었습니다.');
            window.location.href = '/';
        }).fail(function (error) {
            alert(JSON.stringify(error));
        });
    }
};

main.init();
```

window.location.href = '/'
- 글 등록이 성공하면 메인페이지(/)로 이동


### index 변수의 속성으로 function을 추가한 이유?

중복된 함수이름은 자주 발생할 수 있음 

index.js 만의 유효범위를 만들어 사용 

index 객체에서만 function이 유효하기 때문에 다른 JS와 겹칠 위험이 사라짐

footer.mustache에 추가
```html
<script src="/js/app/index.js"></script>
```

</br>

정상 작동 확인

<img width="436" alt="스크린샷 2022-07-08 오후 3 44 06" src="https://user-images.githubusercontent.com/48265714/177932842-c1d70bd9-c2cf-4293-8a63-2985911d83ea.png">

<img width="902" alt="스크린샷 2022-07-08 오후 3 54 40" src="https://user-images.githubusercontent.com/48265714/177934468-6ba10654-c49c-4e72-ba76-a60466af818a.png">


</br>

--- 

### 전체 조회 화면 만들기

index.mustache UI 변경

```html
  {{>layout/header}}
  
      <h1>스프링부트로 시작하는 웹 서비스 Ver.2</h1>
      <div class="col-md-12">
          <div class="row">
              <div class="col-md-6">
                  <a href="/posts/save" role="button" class="btn btn-primary">글 등록</a>
              </div>
          </div>
          <br>
          <!-- 목록 출력 영역 -->
          <table class="table table-horizontal table-bordered">
              <thead class="thead-strong">
              <tr>
                  <th>게시글번호</th>
                  <th>제목</th>
                  <th>작성자</th>
                  <th>최종수정일</th>
              </tr>
              </thead>
              <tbody id="tbody">
              {{#posts}}	// {1}
                  <tr>
                      <td>{{id}}</td>	// {2}
                      <td>{{title}}</td>
                      <td>{{author}}</td>
                      <td>{{modifiedDate}}</td>
                  </tr>
              {{/posts}}
              </tbody>
          </table>
      </div>
  
  {{>layout/footer}}
```

{{#posts}}
- posts List를 순회
- Java의 for문과 동일

{{id}} 등의 {{변수명}}
- List에서 뽑아낸 객체의 필드 사용
- Controller, Service, Repository 코드 작성

</br>

PostsRepository

```java
public interface PostsRepository extends JpaRepository<Posts, Long> {
    @Query("SELECT p FROM Posts p ORDER BY p.id DESC")
    List<Posts> findAllDesc();
}
```

@Query
- SpringDataJpa에서 제공하지 않는 메소드는 쿼리로 작성해도 OK!
(@Query가 가독성이 좋음)

</br>

PostsService

```java
@RequiredArgsConstructor
@Service
public class PostsService {
    private final PostsRepository postsRepository;
  
    ...
  
    @Transactional(readOnly = true)
    public List<PostsListResponseDto> findAllDesc() {
        return postsRepository.findAllDesc().stream()
                .map(PostsListResponseDto::new)
                .collect(Collectors.toList());
    }
}
```

@Transactional(readOnly = true)
- (readOnly = true) : 트랜잭션 범위는 유지하되, 조회 기능만 남겨두어 조회 속도가 개선되기 때문에 등록, 수정, 삭제 기능이 전혀 없는 서비스 메소드에서 사용하는 것을 추천

- 트랜잭션 : 데이터베이스의 상태를 변경시키는 작업 또는 한번에 수행되어야 하는 연산들을 의미, 트랜잭션 작업이 끝나면 Commit 또는 Rollback 되어야 함

.map(PostsListResponseDto::new)

- 실제 : 
```
.map(posts -> new PostsListResponseDto(posts))
```
postsRepository 결과로 넘어온 Posts의 Stream을 map을 통해 PostsListRepositoryDto 변환 → List로 반환하는 메소드

</br>

PostsListResponseDto 추가

```java
@Getter
public class PostsListResponseDto {
    private Long id;
    private String title;
    private String author;
    private LocalDateTime modifiedDate;

    public PostsListResponseDto(Posts entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.author = entity.getAuthor();
        this.modifiedDate = entity.getModifiedDate;
    }
}
```

</br>

Controller 변경

```java 
private final PostsService postsService;
  
      @GetMapping("/")
      public String index(Model model) {
          model.addAttribute("posts", postsService.findAllDesc());
          return "index";
      }
```

Model
- 서버 템플릿 엔진에서 사용할 수 있는 객체를 저장할 수 있음
- postsService.findAllDesc()로 가져온 결과를 posts로 index.mustache에 전달

</br>

<img width="1428" alt="스크린샷 2022-07-08 오후 5 11 32" src="https://user-images.githubusercontent.com/48265714/177947958-eb5bfc87-b282-4e95-8e0a-e8eb8837482d.png">

!* 최종수정일 오류로 일단 제외하고 진행!