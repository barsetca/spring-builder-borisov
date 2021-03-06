## Описание шагов от джуниор кода к реализции подобной Spring.

- по сути рефакторинг джуниор кода с помощью приниципов SOLID и паттерна proxy

**Правила кодаписания:**
1. #### Flexible
2. #### Reusable
3. #### Readable
4. ####SOLID
**S** - **Single responsibility** - 1е правило Принцип единственной отвественности
**O** - **Open Closed Principle** - Система должна быть открыта к дополнениям (напрример кастомные анностации, свои имплементации), 
но должна быть закрыта от изменений в уже написанном закрытом коде коде
(как правило эти принципы ломаются вместе)
**L** - **Liskov Substitution Principle** - Принцип замены Лискова
**I** -  **Interface Segregation Principle** 4е правило SOLID - Ни один клиент не должен зависеть от методов, которые он не использует + не надо делать "толстые" интефейсы (нужно делать тонкие, лучше функциональные интерфейсы)
**D** - **Dependency inversion**   - инверсия зависимости


### ТЗ:
Есть больница/тюрьма с комнатами/камерами, которые надо дизенфицировать от корона вируса. Для этого:
1. Надо всех предупредить, чтобы покинули комнаты
2. Отправить полицейского/охранника чтобы выгнать из комнат всех, кто не вышел
3. првести дизенфекцию
4. Сообщить что можно вернуться в комнаты.

#### Дополнительные опиции:
Запустить рекламу
Предупредить что используются не рекомендуемые классы/методы

**Шаг один.** Делаем простую реализацию - создаем объекты через new
private Announcer announcer = new ConsoleAnnouncer();
private Policeman policeman = new PolicemanImpl();
- это плохо, т.к. идет в разрез с правилами кодописания и нарушается принцип S (single responsibility) из SOLID.
  Здесь от 3 до бесконечности responsibility для каждого объекта new, 
  т.к. получается наш класс CoronaDesinfector отвечает за
  - выбор имплементации интерфейса (у интефеса может быть от 1 до бесконечности)
  - создание объекта (уметь и знать как правильно создать: пустой или нет конструктор и т.д.)
  - его настройку
    
Надо отказываться от такого создания объекта через new

**Шаг два.** Используем фабрику  ObjectFactory singleton (lookup)
private Announcer announcer = ObjectFactory.getInstance().createObject(Announcer.class);
private Policeman policeman = ObjectFactory.getInstance().createObject(Policeman.class);
теперь CoronaDesinfector вообще ничего не знает какие это объекы (использует то что дает фабрика)
Для этого фабрика использует Config (с имплементацией JavaConfig), который должен уметь определять имплементацию
(для этого тспользуем библиотеку org.reflections и встроенный java reflection )
Таким образом мы создали централизованое место для создания всех объектов, это дает:
- гибкость (не надо лазить по коду и менять в нем что-то, м. сделать это через конфигурационный файлБ который будет читать фабрика)
- мы, перед тем как отдать объекты можем их как угодно сложно настраивать, изменять и добавлять логику 
  (например, добавим в Announcer возможность давать рекламу напитков через объект Recommender (RecommenderImpl))
  Примечание: RecommenderImpl использует кастомную @InjectProperty (аналог @Value) над полем,
  значение которой можно задавать через в application.properties
  Но здесь надо быть остожным, чтобы не сломать принципы SOБ т.к. фабрика должна отвечать за создание объекта
  а не его конфигурацию. Для этого создаем интерфейс ObjectConfigurator () аналог n Spring => BeanPostProcessor
  на каждую аннотацию, настройку и т.д. будет своя имплементация ObjectConfigurator`а
  Чаще всего фабрика отвечает за создание сервисов, а это зачастую Stateless синглтоны (а не data object типа Person),
  что также можно реализовать с помощью фабрики через организацию кэша.
  Примечание: Stateless объекты - объекты которые настраиваются один раз при создании и больше не изменяются
  
Все это хорошо но это НЕ инверсия котнтроля! Это lookup!
- Проблемы
1. Т.к. теперь все пользуются нашей фабрикой - большая зависимость от ее реализации: изменение в коде фабрики
 может привести к необходимости в изменении кода всех пользователей фабрики! - Утечка инфраструктуры в бизнес логику
2. При написании unit test объекты создаются через new, что приведет к колосальным затратам времени на каждый тест, 
   т.к. каждый тест будет полностью конфигурировать все объекты
   
3. Реализовывать синглтоны руками (домашний синглтон) - это плохо (это антипаттерн) 
   Здесь как правило объекты связаны между собой статическими методами и unit test нормально не сделать
   
Делаем рефкторинг

**Шаг три.** Инверсия контроля.
- Инверсия контроля = Do not call us, We call you - мы будем сами создавать твои объекты и их настраивать
Создаем еще одну аннотацию для дипенденси инжекшена (@InjectByType) + еще один конфигуратор(InjectByTypeAnnotationObjectConfigurator) и рефакторим
@InjectByType // аналог  @Autowired
private Announcer announcer;
@InjectByType
private Policeman policeman;
Теперь объекты через new (CoronaDesinfector desinfector = new CoronaDesinfector()) создавать нельзя. 
Раньше мы создавали основной объект CoronaDesinfector через new, а он настравивал себя сам, используя ObjectFactory
Теперь сделаем это через фабрику , а она уже его настроит 
CoronaDesinfector desinfector = ObjectFactory.getInstance().createObject(CoronaDesinfector.class);

-  научим нашу систему кэшировать все синглтоны создав ApplicationContext (как в Spring), 
   и специальную анностацию для сервисного класса @Singleton. Рефакторим ObjectFactory для исп-я контекста.
    
- делаем раннер (по аналогии со спрингом) ApplicationRun, 
  в котором создаем основные наши объекты (конфиг, контекст, фабрику) и возвращаем натроенный контекст.
Теперь мы можем отказаться от lookup (ObjectFactory.getInstance().createObject(CoronaDesinfector.class)) в Main,
  а используем контекст для получения объекта
  
Все хорошо,но есть слабое место - теперь нельзя натраивать объекты через конструктор (new не используем),
т.к. объекты настриваются в отдельных классах, для этого предназанченных. Попытка приводит к NPE

**Шаг 4.** SecondFaceConstructor (init метод)
конструктор не работает т.к. инжектятся поля, которые еще не готовы на момент отработки конструктора
по этому вместо
public PolicemanImpl() {
System.out.println(recommender.getClass());
}
надо использовать метод init() - после того как объект настроен, а не создан
но init сам по себе не запустится, по этому надо использовать концепт SecondFaceConstructor 
для этого исп-ся @PostConstruct и для того чтобы она заработала будем использовать
ObjectFactory, где в методе createObject после конфигурации объекта (configure()) запускаем метод
помеченный @PostConstruct у объекта если он у него есть.
Почему в ObjectFactory? Потому что responsibility фабрики - создавать объекты, а init() - это по сути конструктор
Если переносить его в конфигуратор, то этот конфигуратор обязательно д.б. последним, т.е. придем к 
сложному ордерингу...

**Шаг 5.** Proxy.
Будем печать в лог сообщение если используются методы класса, помеченного @Deprecated.
- т.е. хотим изменить логику работы всех методов этого класса.
Идея Proxy pattern - если тебе надо изменить логику метода, а у тебя нет досупа (класс написан не тобой),
  подменяем объект на другой, соответствующий по типу,
  имплементирующий тоже интерфейс (заложено в java dynamic proxy: Proxy.newProxyInstance()) или 
  унаследованный от нашего класса , если интефейса нет (java в коробке не умеет, надо подключать биб-ку cglib: Enhancer.create())
  см. DeprecatedHandlerProxyConfigurator