## 图片加载

```java
ImageLoader.with(context).getImage(uri).into(imageView1);
```

保存原图到本地和缓存在内存中，但是会根据控件大小来压缩图片并最后更新到imageView

主要通过 StatusMessage类来保存每个需要加载的图片的各种信息然后在其中更新UI

#### ImageCache

> 主要用来加载查询内存缓存和本地缓存李是否有没有图片
>
> ```java
> //默认使用APP名称成为本地缓存的目录
> public ImageCache(Context context)
> ```
>
> 传入上下文只要是为了获取app名称，这里默认构造的
>
> ```javav
> /**
>  * 设置文件路径
>  * @param name
>  */
> public void setPath(String name) 
> ```
>
> ```java
> /**
>  * 依次向缓存里放东西
>  * @param uri
>  * @param bitmap
>  * @throws Exception
>  */
> public void putBitmap(String uri, Bitmap bitmap)
> ```
>
> ```java
> /**
>  * 检查本地和内存当中是否有所需图片,并更新状态消息对象的属性值
>  * @param uri
>  * @param statusMessage 用来更新这个状态消息
>  * @throws Exception
>  */
> public void checkBitmap(String uri, StatusMessage statusMessage)
> ```
>
> ```java
> /**
>  * 从本地获取图片
>  * @param uri 图片的网络链接
>  * @param view 准备加载图片的View（用于提供长和宽便于压缩图片）
>  * @return Bitmap
>  * @throws Exception
>  */
> public Bitmap getFromLocal(String uri,View view)
> ```

#### ImageLoader

> 单例模式，主要方法只有三个方法
>
> 
>
> ```java
> public static ImageLoader with(Context context)
> ```
>
> 用来传入上下文，并查询是否存在本类的单例对象
>
> 
>
> ```java
> public StatusMessage getImage(final String uri)
> ```
>
> 根据传入的uri来生成一个状态消息（StatusMessage）实例，并加入到消息管道（map）
>
> 然后根据三级缓存的规则依次检查本地和缓存中是否有所需要的图片，并更新这个状态消息实例
>
> 返回设置好的状态消息实例
>
> 
>
> ```java
> public void getFromInter(final String uri)
> ```
>
> 不多说，通过网络链接获取Bitmap

#### StatusMessage

属性有以下

```java
String uri;
boolean islocalExists = false;
boolean isMemoryExists = false;

ImageCache imageCache;
Future<?> future;
Bitmap bitmap;
//更新完UI后从列表中删除该图片的消息状态，以便于jvm回收
Map<String,StatusMessage> statusMessageList;

ImageView imageView;
```

主要方法

```
/**
 *将图片加载进View
 */
public void into(final ImageView imageView)
```

