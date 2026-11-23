基于考题做以下：
1，找下免费的api和参考下github天气的实现。
2，设计天气主页面UI，整体内容上边是地址切换，接下来有一段是今天的天气，在下面一段是15天的天气。
3，当用户左右滑动时，地址切换，下面整体是viewpage切换。
4，进入有定位功能，需要用户权限。没有定位信息显示北京的天气。
5，位置下面有一行更新时间，在下面一行有地址的index圆点，左右切换时原点切换，圆点做个自定义组件。
6，位置最右边是添加城市按钮，初始只有一个定位城市或北京。
7，当天天气显示温度，天气状态，每个两小时的温度变化曲线，可以左右拖动。
8，15天的天气，也可以左右拖动。
9，天气数据缓存到数据库，进来先加载数据库，更新时间那里显示更新中。更新完显示更新时间。

## 运行方式

在仓库根目录用 JDK 17 和 Android SDK 36 构建，设备需为 Android 7.0 或以上。

```powershell
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:installDebug
```

安装 天气 后，从系统应用列表打开「天气」。天气代码使用 `com.wallet.weather` 包，随 `:app` 一起构建。

## 设计

- `WeatherActivity` 通过 ViewPager2 和 `WeatherPagerAdapter` 切换城市，顶部圆点同步选中状态。每个城市由 `WeatherPageFragment` 绑定 XML 布局，并读取共享的 `WeatherVM` 状态。`item` 下的两个 RecyclerView 适配器分别展示当地时间每两小时的温度曲线和含今天的 15 天预报。
- `WeatherVM` 管理城市、选中位置和请求状态。启动时请求前台粗略定位，定位可用时用当前位置替换默认页，权限被拒绝、定位关闭或超时时显示北京。系统地理编码不可用时使用「当前位置」。
- `api` 定义 Retrofit 接口，`bean` 定义 Open-Meteo 响应字段。`room` 保存添加的城市、每个位置最后一次成功的预报和更新时间，并迁移旧版天气数据库。进入城市页先读缓存，再更新网络。请求失败保留缓存，可在内容顶部下拉刷新或重试。
- Open-Meteo 将实时、逐时和逐日数据合并到一次天气请求中，使用当前地时区。地名搜索另走 Geocoding API，支持城市名或邮编。公开端点适合免费非商业用途，无需 API key。

## 验收场景

1. 首次打开时分别允许和拒绝定位权限，确认显示当前位置或北京。
2. 点击「＋」搜索上海或 Tokyo，选中后确认标题、圆点和天气同步切换。重新打开应用，确认添加的城市仍在。
3. 横向滑动页内空白区域切换城市，再分别拖动温度图和 15 天预报，确认能浏览后续数据。
4. 成功获取一次天气后断网重启，确认缓存仍可查看，状态栏提示更新失败。恢复网络后，在内容顶部下拉重试。

## 参考资料

- [Open-Meteo 天气预报 API](https://open-meteo.com/en/docs)
- [Open-Meteo 地名搜索 API](https://open-meteo.com/en/docs/geocoding-api)
- [Open-Meteo 使用条件](https://open-meteo.com/en/pricing)
- [Breezy Weather 的天气数据源说明](https://github.com/breezy-weather/breezy-weather/blob/main/docs/SOURCES.md)
- 
[apk下载地址](https://github.com/likainian/Weather/blob/master/app/release/app-release.apk)

<img width="1200" height="2664" alt="74b520de132830d08fec3412546464b" src="https://github.com/user-attachments/assets/6db4f6bb-9c23-473d-a227-f8650566fa84" />
