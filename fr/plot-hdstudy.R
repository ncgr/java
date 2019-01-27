hdstudy = read.table("hdstudy.out", header=TRUE)
plot(hdstudy$ctrl.f, hdstudy$case.f,
     main="HD Study", xlab="Control Support Fraction", ylab="Case Support Fraction")
lines(c(0,1), c(0,1), col="red")
