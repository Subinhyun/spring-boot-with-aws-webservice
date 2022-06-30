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
public interface PostRepository extends JpaRepository<Posts, Long> {

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
    PostRepository postRepository;
    
    @After
    public void cleanup() {
        postRepository.deleteAll();
    }
    
    @Test
    public void saveAndFindAll() {
        String title = "test title";
        String content = "test content";
        
        postRepository.save(Posts.builder()
                .title(title)
                .content(content)
                .author("hkhh1029@naver.com")
                .build());
        
        List<Posts> postsList = postRepository.findAll();
        
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

</br>

---

</br>

