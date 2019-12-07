[![](https://jitpack.io/v/dzenm/badge-drawable.svg)](https://jitpack.io/#dzenm/badge-drawable)
### 为Drawable添加一个Badge
---
#### 使用方法: 
1. 在项目的build.gradle文件中添加
    ```
    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }
	```
2. 在app的build.gradle文件中添加依赖
    ```
    dependencies {
        implementation 'com.github.dzenm:badge-drawable:1.0'
    }
    ```
3. 创建一个Badge
    ```
    Bitmap bitmap = new BadgeDrawable.Builder(getApplicationContext())
                .setDrawable(drawableResId)
                .setCircle(isCircle)
                .setInner(isInner)
                .setNumber(number)
                .setBadgePosition(positionBadge)
                .build();
    ```
4. 在需要设置Badge的地方使用
    ```
    imageView.setImageBitmap(bitmap);
    ```


![avatar](https://github.com/dzenm/badge-drawable/tree/master/screenshot/badgedrawable.jpg)

[下载APK](https://github.com/dzenm/badge-drawable/tree/master/apk/app-debug.apk)