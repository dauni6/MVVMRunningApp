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
