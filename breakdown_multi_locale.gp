reset
#set terminal 'wxt'
set terminal 'pdfcairo' color dashed enhanced
#set terminal 'png'
set output '../latex/sections/global-view-multi-locale/figures/breakdown-multi.pdf'
#set title "Breakdown execution time stages"
set key invert reverse Left outside
set key autotitle columnheader
set auto y
set auto x
unset xtics
set xtics nomirror #rotate by -45 scale 0
set style data histogram
set style histogram rowstacked
set style fill solid border -1
set boxwidth 0.75
set xlabel '# locales'
set ylabel 'execution time (s)'

plot 'gantt.data' \
     using 2:xtic(1), for [i=3:5] '' using i
