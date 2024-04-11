# 上班等公車
使用現代 Android 技術開發，復刻公車 App 基本功能，並針對上班族最常用的功能做 Redesign 將常用站牌放在首頁，<br>
資料採用 TDX Open API，並利用 OData 語法過濾與限定欄位，增進效能

## Practice
致力於達到官方建議的幾種最佳實踐︰
- MVVM 架構 (UI Layer - Data Layer)
- Dependency injection
- Unidirectional data flow 單向資料流
- Single source of truth 單一可靠值來源

## Test
建立嚴格的 DI 模式，方便替換依賴物件達到隔離測試，確保測試的獨立性、可重複(運行結果一致)且執行快速

## Environment
```
Android Studio Hedgehog | 2023.1.1 Patch 2
Kotlin 1.9.0
compileSdk = 34
minSdk = 24
targetSdk = 34
jvmTarget = "1.8"
```