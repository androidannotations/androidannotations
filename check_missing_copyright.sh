#!/bin/bash
FILES=`git diff --name-only HEAD 5ea91f453d4b1ec3d3f3d08447b419a668f97c71`
HAS_MISSING=false
for f in $FILES
do
  # take action on each file. $f store current file name
  hasCopyright=$(grep "Copyright (" $f  2>&-)
  if [ $? -eq 0 ] && [ $f != "check_missing_copyright.sh" ]; then
    hasAACopyright=$(echo "$hasCopyright" | grep "the AndroidAnnotations project")
    if [ $? -ne 0 ]; then
      echo "$f"
      echo "$hasCopyright"
      echo "$hasAACopyright"

      HAS_MISSING=true
    fi
  fi
done

if [ $HAS_MISSING == true ]; then
  echo "Please add second copyright line: 'Copyright (C) 2016-<current> the AndroidAnnotations project' for the files listed above!"
  exit 1
fi
