ps | grep java | grep -v grep | awk '{print $1}' | xargs kill -9
