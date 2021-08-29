# MVVMRunningApp
 코루틴, 힐트를 사용하여 러닝앱을 만들어보자


 ## part17. Canceling a Run
 <pre>
 1. TrackingFragment에서 tracking을 시작하면 계속 X(메뉴)가 나오지 않았다.
 TrackingFragment의 onCreateView()에서 etHasOptionsMenu(true)를 했음엗 불구하고 나오지 않아 확인해보니,
 앱 theme이 NoActionBar인데, MainActivity의 onCreate()에서 setSupportActionBar()에 toolbar를 넣어주지 않아 생긴 문제였다.
 기본적으로 Fragment에서 메뉴를 사용하려면 해당 Fragment의 host에서 setSupportActionBar()가 정의되어 있어야만 보인다.
 https://stackoverflow.com/questions/20226897/oncreateoptionsmenu-not-called-in-fragment
 </pre>

 ## part18. Saving a Run in the Database
 <pre>
 1. Snackbar를 사용할 때 view가 필요하다. tracking을 저장하면 navigate()를 이용하여 RunFragment로 이동하는데, 이때 TrackingFragment의 view를 사용할 수 없음에 주의하자. (해당 코드에 더 자세한 설명이 있음)
 </pre>

 ## part19. Showing Runs in the RecyclerView
 <pre>
 1. 어떠한 비동기 콜도 없이 LiveData를 통해서 Room DB에 접근하여 데이터를 가져온다. LiveData자체가 백그라운드에서 비동기로 돌고 있으며 Room에서 보내주는 data를 받을때까지 대기하다가
 emit되면 그 데이터를 홀드하고 ViewModel에 변화를 알리게 되는 것 이다.
 </pre>

 ## part20. Sorting Runs
 <pre>
 1. MediatorLiveData를 사용하여 각각의 라이브데이터를 SorType에 따라 동작될 수 있도록 수정하였다.
 addSource()의 콜백에 따라 동작되는 것 같다. 그런데 뭔가 bolierplate가 많은 느낌이 든다. 어떻게하면 더 보기좋게 수정할 수 있을까?
 우선 강의에서는 Spinner의 각 아이템들의 위치를 아니까 그냥 0,1,2,3,4로 넣었는데, 나는 그냥 SortType의 생성자로 position을 만들어서 최대한 매직넘버처럼 보이지 않도록 하였다.
 </pre>

 ## part21. Saving Username and Weight
 <pre>
 1. SharedPreference를 통해 첫 사용자가 runFragment로 가기전에 입력한 데이터를 저장한다.
 2. AppModule에 앱 생명주기 전반에 걸쳐 사용할 name, weight, firstTimeToggle에 대한 의존성을 만들어준다
 3. 이미 접속한 이력이 있는 사람이면 setupFragment를 거치지 않고 바로 runFragment로 보내준다. 이때 뒤로가기를 하면 다시 setupFragment가 나오지 않도록 하기 위해
 runFragment로 가기전에 navOptions를 이용하여 setupFragment를 반드시 백스택에서 제거해준다
 </pre>

 ## part22. Total Run Statistics & SettingsFragment
 <pre>
 1. SettingsFragment랑 StatisticsFragment 수정. SettingsFragment에는 이름이나 몸무게를 다시 SharedPreference에 저장하고
 StatisticsFragment에는 total정보들을 StatisticsViewModel에 만들어놓은 LiveData를 observe하여 변경하도록 함
 </pre>

 ## part23. Setting up the BarChart
 <pre>
 1. BarChart로 변경하고 적용하기
 </pre>

 ## part24. Bug fix
 <pre>
 1. tracking을 start하고 x버튼을 누른 뒤 다시 tracking을 하려고 trackingFragment에 들어가면 다시 시작임엗 불구하고 FINISH RUN 버튼이 보이는 오류 수정
 2. CancelDialog가 보이는 상태에서 화면을 portrait -> landscape로 전환하면 dialog가 사라지는 오류 수정
 (CustomCancelDialog를 만들었고, 만들때 parentFragmentManager를 사용했다. TrackingFragment가 destory된 뒤 다시 만들어지면 기존에 사용했던 MaterialAlertDialogBuilder는
 TrackingFragment의 생명주기를 따르기 때문에 화면전환 시 없어지기 때문이다. 또한 CustomCancelDialog에서 positiveListener를 통해서 dialog를 제어함. 또한 반드시 CancelDialog가 TAG를 가지고 있어야만 한다.
  그렇지 않으면 화면전환시에는 CustomCancelDialog가 살아있지만 화면전환 시 CustomCancelDialog의 positiveListener는 초기화가 null이기 때문에 positiveListener가 동작하지 않게 된다.
  반드시 TAG가 있어야 하며 화면전환시 뷰가 파괴되어 다시 TrackingFragment가 onViewCreated()를 호출하게 될 때 savedInstanceState를 통해 TAG를 통해서 데이터를 가져온 뒤 positiveListener를 동작시킬 수 있게 된다)
 3. 간헐적으로 조금전에 저장했던 tarcking time이 다시 trackingFragment에 들어갔을 때 보이는 오류 수정
 4. BottomNavigation에서 동일한 메뉴를 누르면 다시 리로드되는 현상 수정(ex: runFragment 메뉴를 누르면 변경없어야하는데 다시 리로드됨)
 추가로 setOnNavigationItemReselectedListener를 사용하려 했으나 deprecated되어 setOnItemReselectedListener로 변경함
 </pre>
