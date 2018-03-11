#!/bin/awk -f
{
    sum+=$2
    name=$1
}
END {
    print "Average " name
    print sum/ NR
}
