# addressLib
自定义的收货地址控件

1.下载后作为依赖添加到项目中

    compile project(':addresslib')

2.布局文件中添加控件

    <!--添加的自定义控件-->
    <skxy.dev.addresslib.ReceviceAdressView
        android:id="@+id/address"
        android:layout_width="match_parent"
        android:layout_height="45dp"
        />

3.获取控件中的地址

    mAdressView = (ReceviceAdressView) findViewById(R.id.address);
    //获取自定义控件中的地址
    String address = mAdressView.getAddress();
    
4.设置地址图标是否可见

    mAdressView.setAddressIv(false);
