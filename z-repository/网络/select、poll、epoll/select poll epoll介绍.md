


### select模型
```
理解select模型的关键在于理解fd_set,为说明方便，取fd_set长度为1字节，fd_set中的每一bit可以对应一个文件描述符fd。则1字节长的fd_set最大可以对应8个fd。

（1）执行fd_set set; FD_ZERO(&set);则set用位表示是0000,0000。
（2）若fd＝5,执行FD_SET(fd,&set);后set变为0001,0000(第5位置为1)
（3）若再加入fd＝2，fd=1,则set变为0001,0011
（4）执行select(6,&set,0,0,0)阻塞等待
（5）若fd=1,fd=2上都发生可读事件，则select返回，此时set变为0000,0011。注意：没有事件发生的fd=5被清空。

```








### epoll模型

网上很多人都说，对监听的fd集合使用了共享内存， 这其实是错误的。
并不是使用了共享内存，而是在epoll_ctl函数中已经将fd添加到了内核中，所在在之后的epoll_wait中，并不需要一直在用户态和内核态之间复制fd的链表。


```java
int epoll_create(int size);  
int epoll_ctl(int epfd, int op, int fd, struct epoll_event *event);                  //  注意这里是evnet, 为单个fd的注册的事件（读写等）
int epoll_wait(int epfd, struct epoll_event *events,int maxevents, int timeout);     //  这里为events， 用于接受回传的待处理的event数组。
```




### 参考资料
1. https://www.jianshu.com/p/34ebdee8bb6f
2. https://blog.csdn.net/lyztyycode/article/details/79491419
3. http://www.loujunkai.club/network/selece-poll.html