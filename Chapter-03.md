## Chapter 03 스프링부트에서 JPA로 데이터베이스 다뤄보자

</br>

### JPA란?

객체를 관계형 데이터베이스에서 관리하는 것이 중요

- 애플리케이션 코드 < SQL

- 패러다임 불일치 

JPA가 중간에서 패러다임 일치!

즉, 객체지향적 프로그래밍을 하고, SQL에 종속적인 개발을 하지 않아도 된다.

</br>

### Spring Data JPA

```
JPA <- Hibernate <- Spring Data JPA
```

- 구현체의 용이성
- 저장소 교체의 용이성

</br>

### 요구사항 분석

게시판 기능
- 게시글 조회, 등록,수정,삭제

회원 기능
- 구글/네이버 로그인
- 로그인한 사용자 글 작성 권한
- 본인 작성 글에 대한 권한 관리

</br>

### 프로젝트에 Spring Data Jpa 적용하기

build.gradle에 추가

```
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
runtimeOnly 'com.h2database:h2'
```

spring-boot-starter-data-jpa
- 스프링부트용 Spring Data JPA 추상화 라이브러리
- 스프링부트 버전에 맞춰 자동으로 JPA 관련 라이브러리들의 버전을 관리

h2
- 인메모리 관계형 데이터베이스
- 별도의 설치가 필요없이 프로젝트 의존성만으로 관리할 수 있음
- 메모리에서 실행되기 때문에 애플리케이션을 재시작할 때마다 초기화된다는 점을 이용하여 테스트 용도로 많이 사용됨


</br>

---

</br>

```
@Getter
@NoArgsConstructor
@Entity
public class Posts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private  String author;

    @Builder
    public Posts(String title, String content, String author) {
        this.title = title;
        this.content = content;
        this.author = author;
    }
}
```

Entity
- 테이블과 링크될 클래스임을 나타냄
- 기본값으로 클래스의 카멜케이스 이름을 언더스코어 네이밍으로 테이블 이름을 매칭
    ex) SalesManager.java → sales_manager table

Id
- 해당 테이블의 PK 필드를 나타냄

GeneratedValue
- PK의 생성 규칙을 나타냄

Column 
- 테이블의 칼럼을 나타내며 굳이 선언하지 않아도 해당 클래스의 필드는 모두 칼럼이 됨
- 기본값 외에 추가로 변경이 필요한 옵션이 있으면 사용
- 문자열의 경우 VARCHAR(255)가 기본값인데, 사이즈를 500으로 늘리고 싶거나, 타입을 TEXT로 변경하고 싶거나 등의 경우에 사용됨

NoArgsConstructor
- 기본 생성자 자동 추가
- public Posts(){}와 같은 효과 

Getter 
- 클래스 내 모든 필드의 Getter 메소드를 자동생성

Builder
- 해당 클래스의 빌더 패턴 클래스를 생성
- 생성자 상단에 선언 시 생성자에 포함된 필드만 빌더에 포함


#### * Entity 클래스에서는 절대 Setter 메소드를 만들지 않는다
getter/setter를 무작정 생성하는 경우가 있다. 이렇게 되면 해당 클래스의 인스턴스 값들이 언제 어디서 변해야 하는지 코드 상으로 명확하게 구분할 수 없어 차후 기능 변경 시 복잡해진다.


</br>

---

</br>

```
public interface PostsRepository extends JpaRepository<Posts, Long> {

}
```

인터페이스를 생성 후, 
JpaRepository<Entity 클래스, PK 타입>를 상속하면 기본적인 CRUD 메소드가 자동 생성

</br>

---

</br>

### Spring Data JPA 테스트 코드 작성하기

```
@RunWith(SpringRunner.class)
@SpringBootTest
public class PostsRepositoryTest {
    @Autowired
    PostsRepository postsRepository;
    
    @After
    public void cleanup() {
        postsRepository.deleteAll();
    }
    
    @Test
    public void saveAndFindAll() {
        String title = "test title";
        String content = "test content";
        
        postsRepository.save(Posts.builder()
                .title(title)
                .content(content)
                .author("hkhh1029@naver.com")
                .build());
        
        List<Posts> postsList = postsRepository.findAll();
        
        Posts posts = postsList.get(0);
        assertThat(posts.getTitle()).isEqualTo(title);
        assertThat(posts.getContent()).isEqualTo(content);
    }
}
```

After 
- Junit에서 단위 테스트가 끝날 때마다 수행되는 메소드를 지정
- 보통은 배포 전 전체 테스트를 수행할 때 테스트간 데이터 침범을 막기 위해 사용
- 여러 테스트가 동시에 수행되면 테스트용 데이터베이스인 H2에 데이터가 그대로 남아 있어 다음 테스트 실ㄹ행 시 테스트가 실패할 수 있음

postsRepository.save
- 테이블 posts에 insert/update 쿼리를 실행
- id 값이 있다면 update가, 없다면 insert 쿼리가 실행됨

postsRepository.findAll
- 테이블 posts에 있는 모든 데이터를 조회해오는 메소드

</br>

---

</br>

### 실제로 실행된 쿼리는 어떤 형태일까?

resources > application.properties에 추가

```
spring.jpa.show_sql=true
```

<img width="1048" alt="스크린샷 2022-06-30 오후 2 30 55" src="https://user-images.githubusercontent.com/48265714/176600416-0b9dd309-7279-4131-9d7d-7f91bc794c3c.png">

</br>

출력되는 쿼리 로그를 MySQL 버전으로 변경 :

resources > application.properties에 추가

```
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL5InnoDBDialect
```

Spring Boot h2 memtestdb not found error

https://velog.io/@lehdqlsl/spring-boot-h2-memtestdb-not-found-%EB%AC%B8%EC%A0%9C

</br>

---

</br>

### 등록/수정/조회 API 만들기

- Request 데이터를 받을 Dto
- API 요청을 받을 Controller
- 트랜잭션, 도메인 기능 간의 순서를 보장하는 Service

Spring 웹 계층
- Web Layer : 컨트롤러와 JSP/Freemarker 등의 뷰 템플릿 영역. 이외에도 필터, 인터셉터, 컨트롤러 어드바이스 등 외부 요청과 응답에 대한 전반적인 영역을 이야기 한다.
- Service Layer : @Service에 사용되는 서비스 영역으로 일반적으로 Controller와 Dao의 중간 영역에서 사용. @Transactional이 사용되어야 하는 영역이기도 하다.
- Repostiroy Layer: Database오 같이 데이터 저장소에 접근하는 여역(Dao영역)
- Dtos : Dto는 계층 간에 데이터 교환을 위한 객체를 이야기 하며 Dtos는 이들의 영역
- Domain Model : 도메인이라 불리는 개발 대상을 모든 사람이 동일한 관점에서 이해할 수 있고 공유할 수 있도록 단순화 시킨 것을 도메인 모델이라고 한다.

</br>

---

</br>

#### 등록 기능 추가

PostsApiController

```
@RequiredArgsConstructor
@RestController
public class PostsApiController {
    private final PostsService postsService;
    
    @PostMapping("/api/v1/posts")
    public Long save(@RequestBody PostsSaveRequestsDto requestsDto) {
        return postsService.save(requestsDto);
    }
}
```

PostsService

```
@RequiredArgsConstructor
@Service
public class PostsService {
    private final PostRepository postRepository;
    
    @Transactional
    public Long save(PostsSaveRequestsDto requestsDto) {
        return postRepository.save(requestsDto.toEntity()).getId();
    }
}
```

Spring에서 Bean을 주입 받는 방식
- Autowired
- setter
- 생성자(가장 권장하는 방식)

생성자로 Bean 객체를 받도록 하면 @Autowired와 동일한 효과

PostsSaveRequestsDto

```
@Getter
@NoArgsConstructor
public class PostsSaveRequestsDto {
    private String title;
    private String content;
    private String author;
    
    @Builder
    public PostsSaveRequestsDto(String title, String content, String author) {
        this.title = title;
        this.content = content;
        this.author = author;
    }
    
    public Posts toEntity() {
        return Posts.builder()
                .title(title)
                .content(content)
                .author(author)
                .build();
    }
}
```

PostsApiControllerTest

```
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PostsApiControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PostsRepository postsRepository;

    @After
    public void tearDown() throws Exception {
        postsRepository.deleteAll();
    }

    @Test
    public void postSave() throws Exception {
        String title = "title";
        String content = "content";
        PostsSaveRequestsDto requestsDto = PostsSaveRequestsDto.builder()
                .title(title)
                .content(content)
                .author("author")
                .build();

        String url = "http://localhost:" + port + "/api/v1/posts";

        ResponseEntity<Long> responseEntity = restTemplate.postForEntity(url, requestsDto, Long.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isGreaterThan(0L);

        List<Posts> all = postsRepository.findAll();
        assertThat(all.get(0).getTitle()).isEqualTo(title);
        assertThat(all.get(0).getContent()).isEqualTo(content);
    }
}
```

#### 수정/조회 기능 추가

PostApiController에 추가

```
@PutMapping("/api/v1/posts/{id}")
public Long update(@PathVariable Long id, @RequestBody PostsSaveRequestsDto requestsDto) {
    return postsService.update(id, requestsDto);
}

@GetMapping("/api/v1/posts/{id}")
public PostsResponseDto findById(@PathVariable Long id) {
    return postsService.findById(id);
}
```

PostResponseDto

```
@Getter
public class PostsResponseDto {
    private Long id;
    private String title;
    private String content;
    private String author;

    public PostResponseDto(Posts entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.content = entity.getContent();
        this.author = entity.getAuthor();
    }
}
```

PostsUpdateRequestDto

```
@Getter
@NoArgsConstructor
public class PostsUpdateRequestDto {
    private String title;
    private String content;
    
    @Builder
    public PostsUpdateRequestDto(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
```

Posts

```
public void update(String title, String content) {
    this.title = title;
    this.content = content;
}
```

PostsService

```
@Transactional
public Long update(Long id, PostsSaveRequestsDto requestsDto) {
    Posts posts = postRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id));
    posts.update(requestsDto.getTitle(), requestsDto.getContent());
    return id;
}

@Transactional
public PostsResponseDto findById(Long id) {
    Posts entity = postRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id));
    return new PostsResponseDto(entity);
}
```

### JPA 영속성 컨텍스트 
영속성 컨텍스트 : 엔티티르 영구 저장하는 환경

JPA의 핵심 내용은 엔티티가 영속성 컨텍스트에 포함되어 있냐 아니냐로 갈린다. JPA의 엔티티 매니저가 활성화된 상태로 트랜잭션 안에서 데이터베이스에서 데이터를 가져오면 이 데이터는 영속성 컨텍스트가 유지된 상태다. 이 상태에서 해당 값을 변경하면 트랜잭션이 끝나는 시점에 해당 테이블에 변경분을 반영한다. 즉 Entity 객체의 값만 변경하면 별도로 update쿼리를 날릴 필요가 없다는 것이다. 이 개념을 더티 체킹이라고 한다.

PostsApiControllerTest

```
@Test
public void postUpdate() throws Exception {
    Posts savedPosts = postsRepository.save(Posts.builder()
                    .title("title")
                    .content("content")
                    .author("author")
                    .build());

    Long updateId = savedPosts.getId();
    String expectedTitle = "title2";
    String expectedContent = "content2";

    PostsUpdateRequestDto requestDto = PostsUpdateRequestDto.builder()
            .title(expectedTitle)
            .content(expectedContent)
            .build();

    String url = "http://localhost:" + port + "/api/v1/posts/" + updateId;

    HttpEntity<PostsUpdateRequestDto> requestEntity = new HttpEntity<>(requestDto);
        
    ResponseEntity<Long> responseEntity = restTemplate.exchange(url, HttpMethod.PUT,requestEntity,Long.class);


    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(responseEntity.getBody()).isGreaterThan(0L);

    List<Posts> all = postsRepository.findAll();
    assertThat(all.get(0).getTitle()).isEqualTo(expectedTitle);
    assertThat(all.get(0).getContent()).isEqualTo(expectedContent);
}
```

</br>

---

</br>

### JPA Auditing으로 생성시간/수정시간 자동화하기

BaseTimeEntity

```
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;

}
```

MappedSuperclass
- JPA Entity 클래스들이 BaseTimeEntity을 상속할 경우 필드들도 칼럼으로 인식하도록 함

EntityListeners(AuditingEntityListner.class)
- BaseTimeEntity 클래스에 Auditing 기능을 포함시킴

CreatedDate
- Entity가 생성되어 저장될 때 시간이 자동 저장

LastModifiedDate
- 조회한 Entity의 값을 변경할 때 시간이 자동 저장

</br>

Application에 어노테이션 추가
```
@EnableJpaAuditing
```

</br>

### JPA Auditing 테스트 코드 작성하기

PostsRepositoryTest에 추가

```
@Test
public void BaseTimeEntitySave() {
    LocalDateTime now = LocalDateTime.of(2019,6,4,0,0,0);
    postsRepository.save(Posts.builder()
                .title("title")
                .content("content")
                .author("author")
                .build());

    List<Posts> postsList = postsRepository.findAll();

    Posts posts = postsList.get(0);

    System.out.println(">>>>>>>>>>>>>>> createDate = " + posts.getCreatedDate()
                + ", modifiedDate = " + posts.getModifiedDate());


    assertThat(posts.getCreatedDate()).isAfter(now);
    assertThat(posts.getModifiedDate()).isAfter(now);
}
```

