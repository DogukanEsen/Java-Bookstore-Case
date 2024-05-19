# Bookstore Projesi

## Proje Kurulumu

Proje çalıştırdığında eğer veri tabanında kitap yoksa otomatik 5 tane kitap ekler. Proje dockerıze edildiği için daha esnek, ölçeklenebilir ve CI/CD süreçleri daha yönetilebilir olmuştur.

Kurulum için ilk yapılması gereken uygulamanın ana dizininde `docker-compose build` terminal komutunu çalıştırmaktır. Bu komut sayesinde maven ile proje derlenir ve docker içerisinde spring boot uygulamamız çalışmaya hazır olur ve veri tabanımız kurulur. Bu işlem sonrasında `docker-compose up` terminal komutu ile containerımız çalışır. Veri tabanı, derleme işlemi ve proje ayağa kaldırma işlemi bu komutlar ile sağlanır.

## Proje Bağımlılıkları

- spring-boot-starter-data-jpa
- spring-boot-starter-security
- spring-boot-starter-web
- flyway-core
- postgresql
- spring-boot-starter-test
- spring-security-test
- jjwt
- jaxb-api
- jaxb-runtime

## Proje yapısı

Projede modülerliği arttırması, kodun daha iyi anlaşılması ve daha temiz olması için facade tasarım deseni kullanılmıştır.

## Veri Tabanı

Veri tabanı oluşturmasının, geliştirmesinin daha rahat olması ve paylaşılabilirliği arttırmak için flyway migration ile oluşturulmuştur.

## Proje Sınıfları

### PasswordEncoderConfig

Securityde kullanılan bCryptPasswordEncoder' ın clean kod prensiplerine uyarak yapılandırılmış halidir.

### AuthController

Kullanıcı kayıt, giriş ve çıkış işlemlerinin gerçekleşmesini sağlayan rest controllerdır. Kullanıcıdan aldığı değerleri UserService ve LoginUserService servislerine iletir.

### BookController

Tüm kitapların getirilmesini sağlayan controllerdır. Aldığı isteği BookService servisine iletir.

### CartController

Kullanıcıların sepetine kitap eklemesini, kitap çıkarmasını sağlar. Kullanıcıların sepetinin görüntülenmesi ve sepetteki kitapların ödenmesi işlemini gerçekleştirir. Aldığı değerleri CartService servisine iletir.

## DTO Klasörü

Kullanıcılardan alınan veya kullanıcılara gönderilen verilerin direkt entity sınıflara erişmesinden kaynaklanan esneklik, güvenlik gibi sorunların çözümü için dtolar kullanılmıştır. Her entity sınıfın dtoya çevrilmesi için converter metodu vardır.

## Exception Klasörü

Hata yönetiminin iyi bir şekilde ele alınması için ControllerAdvice ve custom exceptionlar kullanılmıştır. Özel hata mesajları sayesinde hata kontrolü sağlanmıştır. Özel hatalarda RuntimeException kullanılma sebebi GeneralExceptiondaki thread ölmesin diyedir.

## Model Klasörü

Veri tabanıyla eşleşen etity tablelar burada oluşturulmuştur. Birbiriyle ilişkileri OnetoOne ve ManytoOne şeklinde tanımlanmıştır. Cart sınıfı kullanıcının sepetini temsil eder. Cart item ise kullanıcının sepetindeki kitabı, kitabın sayısını ve ait olduğu Cartı temsil eder.

## Repository Klasörü

Veri erişim katmanında kullanılan DAOlar burada oluşturulmuştur. Veri tabanına erişim için bu repositoryler kullanılır. JpaRepository ile Dependency Injection yapılmıştır. Bu sayede kod tekrarının önüne geçilir ve özel sorgular daha kolay şekilde yazılmıştır.

## Security Klasörü

Jwt ile kullanıcı ve endpoint güvenliği bu klasörde sağlanmıştır. JwtAurhFilter ile jwt filtresi çalıştırılır, OncePerRequestFilter filter ile çalışır yani her http isteği bir kez çalıştırılır. JwtTokenUtil ile token oluşturma, analiz ve doğrulama işlemleri gerçekleştirilir. Security config ile endpointlerin erişebilirliği düzenlenir, SessionManagement ve csrf devre dışı bırakılmıştır. Kimlik doğrulama işlemleri yapılır.

## BookService

Kitap ekleme, Isbn ile kitap bulma ve veri tabanındaki kitapları getirme burada gerçekleşir. Isbn ile kitap bulunamadığında hata mesajı verir.

## CartService

Jwt ile sepet getirme, sepete kitap ekleme, sepetten kitap silme, sepeti getirme ve ödeme işlemleri burada gerçekleşir. Kitap stoğundan fazla kitap eklenmesi, Sepetten fazla ürün silinmesi, negatif değer eklenmeye çalışması gibi durumlarda hata mesajı verir.

## CreateCartService

Sepet oluşturma işlemi bu service de gerçekleştirilir. CartService de sepet oluşturma işlemi olduğunda döngü hatası olduğundan ayrı service olarak ele alınmıştır. user değeri null olduğunda hata verir.

## TokenService

Logout işleminde kullanılan geçersiz jwt tokenları burada listede tutulur. Bu sayede logout edilmiş jwtler geçersiz sayılır.

## UserService

Securityde kullanılması için UserDetailsService implement edilmiştir. Username ile kullanıcı getirme(UserDetails dönen), Username ile kullanıcı getirme(User dönen), Kullanıcı kayıtı, Gelen requestten username dönen, User id ile user dönen ve logout işlemini TokenService ile yapan durumlar burada gerçekleşir. Username bulunamadı, Username mevcut hataları döner.

## LoginUserService

Kullanıcı giriş işlemi burada gerçekleşir. UserService de kullanıcı giriş işlemi olduğunda döngü hatası olduğundan ayrı service olarak ele alınmıştır. null ve invalid username hataları döner.

## GetDetailsFromRequestService

Jwtden kullanıcı verilerini dönen servistir. Daha temiz kod olması açısından ayrı serviste yazılmıştır. Bu servis sayesinde diğer servisler jwt ile uğraşmadan id ile işlerini tamamlayabilirler.

# Projedeki Endpointler

### 1. **/auth/registerUser** Post isteği alır. Kullanıcı kaydında kullanılır.

### 1. **/auth/loginUser** Post isteği alır. Kullanıcı girişinde kullanılır.

### 1. **/auth/logout** Post isteği alır kullanıcı çıkışında kullanılır.

### 1. **/book** Get isteği alır. Tüm kitapların getirilmesinde kullanılır.

### 1. **/cart/addItem** Post isteği alır. Kullanıcının sepetine kitap eklemesinde kullanılır.

### 1. **/cart/deleteItem** Post isteği alır. Kullanıcının sepetinden kitap çıkarılmasında kullanılır.

### 1. **/cart/items** Get isteği alır. Kullanıcının sepetini görüntülemesinde kullanılır.

### 1. **/cart/payment** Post isteği alır. Kullanıcın ödeme mekanizmasında kullanılır.

# Test Raporları

Testlerde Junit5 ve Mockito kullanılmıştır. Projede 70 test vardır ve proje tüm testlerden geçmiştir.

## Unit Testler

### BookServiceTest

1. Geçerli istek ile kitap kaydetme
1. Geçerli istek ile tüm kitapları getirme
1. Geçerli istek ile isbn kullanarak kitabı getirme
1. Geçersiz istek ile isbn kullanarak Custom error alma

### TokenServiceTest

1. Geçerli istek ile token ekleme
1. Geçerli istek ile token kontrol etme
1. Geçersiz istek ile token kontrol etme ve False alma

### LoginUserServiceTest

1. Geçerli istek ile login çağırma
1. Geçersiz istek ile login çağırma ve custom error alma
1. Null istek ile login çağırma ve error alma

### UserServiceTest

1. Geçerli istek ile username kullanarak UserDetails getirme
1. Geçersiz istek ile geçersiz username kullanarak UserDetails getirme ve error alma
1. Geçersiz istek ile null username kullanarak UserDetails getirme ve error alma

---

1. Geçerli istek ile username kullanarak User getirme
1. Geçersiz istek ile geçersiz username kullanarak User getirme ve error alma
1. Geçersiz istek ile null username kullanarak User getirme ve error alma

---

1. Geçerli istek ile kullanıcı kaydı
1. Geçersiz istek ile kayıtlı kullanıcı kullanarak kullanıcı kaydı ve custom error alma
1. Geçersiz istek ile null kullanıcı kullanarak kullanıcı kaydı ve error alma

---

1. Geçerli istek ile requestten username çıkarma
1. Geçersiz istek ile geçersiz token kullanarak requestten username çıkarma ve error alma
1. Geçersiz istek ile null token kullanarak requestten username çıkarma ve error alma

---

1. Geçerli istek ile kullanıcı idsi kullanarak kullanıcıyı getirme
1. Geçersiz istek ile geçersiz kullanıcı idsi kullanarak kullanıcıyı getirme ve error alma
1. Geçersiz istek ile null kullanıcı idsi kullanarak kullanıcıyı getirme ve error alma

---

1. Geçerli istek ile geçerli token kullanarak Logout işlemi
1. Geçersiz istek ile geçersiz token kullanarak başarısız logout işlemi
1. Geçersiz istek ile null token kullanarak logout işlemi ve error alma

### CreateCartServiceTest

1.  Geçerli istek ile Sepet oluşturma işlemi
1.  Geçersiz istek ile invalid user kullanarak sepet oluşturma işlemi ve error alma
1.  Geçersiz istek ile null user kullanarak sepet oluşturma işlemi ve error alma

### CartServiceTest

1.  Geçerli istek ile sepete kitap ekleme
1.  Geçersiz istek ile invalid isbn ile sepete kitap ekleme ve error alma
1.  Geçersiz istek ile invalid user id ile sepete kitap ekleme ve error alma
1.  Geçersiz istek ile negatif miktar ile sepete kitap ekleme ve error alma
1.  Geçersiz istek ile stoktan fazla miktar ile sepete kitap ekleme ve error alma
1.  Geçersiz istek ile Null isbn ile sepete kitap ekleme ve error alma
1.  Geçersiz istek ile Null CartItemDTO ile sepete kitap ekleme ve error alma
1.  Geçersiz istek ile Null user id ile sepete kitap ekleme ve error alma

---

1.  Geçerli istek ile sepetten kitap çıkarma
1.  Geçersiz istek ile invalid isbn ile sepetten kitap çıkarma ve error alma
1.  Geçersiz istek ile invalid user id ile sepetten kitap çıkarma ve error alma
1.  Geçersiz istek ile negatif miktar ile sepetten kitap çıkarma ve error alma
1.  Geçersiz istek ile stoktan fazla miktar ile sepetten kitap çıkarma ve error alma
1.  Geçersiz istek ile Null isbn ile sepetten kitap çıkarma ve error alma
1.  Geçersiz istek ile Null CartItemDTO ile sepetten kitap çıkarma ve error alma
1.  Geçersiz istek ile Null user id ile sepetten kitap çıkarma ve error alma

---

1.  Geçerli istek ile cart items getirme
1.  Geçersiz istek ile invalid user id ile cart items getirme ve error alma

---

1.  Geçerli istek ile ödeme getirme
1.  Geçersiz istek ile invalid user id ile ödeme getirme ve error alma
1.  Geçersiz istek ile null user id ile ödeme getirme ve error alma

---

1.  Geçerli istek ile kitap miktarı güncellemesi
1.  Geçersiz istek ile geçersiz sepet ile kitap miktarı güncellemesi ve null dönmesi
1.  Geçersiz istek ile null sepet ile kitap miktarı güncellemesi ve null dönmesi
1.  Geçersiz istek ile null isbn ile kitap miktarı güncellemesi ve null dönmesi
1.  Geçersiz istek ile null CartItemDTO ile kitap miktarı güncellemesi ve null dönmesi

---

1.  Geçerli istek ile stoktan az miktar kullanarak stok dönmesi
1.  Geçerli istek ile stoktan fazla miktar kullanarak stok dönmesi
1.  Geçersiz istek ile null kullanarak stok dönmesi ve hata alınması

---

1.  Geçerli istek ile user id ile sepet getirme
1.  Geçersiz istek ile invalid user id ile user id ile sepet getirme ve error alma
1.  Geçersiz istek ile null user id ile user id ile sepet getirme ve error alma

### GetDetailsFromRequestServiceTest

1.  Geçerli jwt ile username bilgisinin alınması
1.  NoAuthorizationHeader ile username bilgisinin alınması ve hata dönmesi
1.  Geçersiz jwt ile username bilgisinin alınması ve error dönmesi
1.  Null token ile username bilgisinin alınması ve error dönmesi
1.  Geçerli jwt ile user id bilgisinin alınması
1.  Geçersiz jwt ile user id bilgisinin alınması ve hata dönmesi

## Controller Testleri

Controller testleri ekran görüntüleri içerikteki word dosyasında açıklamalarıyla belirtilmiştir.
