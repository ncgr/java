hdstudy.all = read.table("hdstudy.all.out", header=TRUE)
hdstudy.rc  = read.table("hdstudy.rc.out", header=TRUE)


plot(hdstudy.all$ctrl.n,
     hdstudy.all$case.n,
     main="HD Study FRs",
     xlab="ctrl sample support",
     ylab="case sample support",
     xlim=c(0,10),
     ylim=c(0,27),
     col="darkgreen"
     )

points(hdstudy.rc$ctrl.n,
       hdstudy.rc$case.n,
       col="darkred",
       )

lines(c(0,10), c(0,27), col="gray")

legend(c("children", "roots"),
       pch=1,
       col=c("darkgreen","darkred"),
       x="topleft")
       

