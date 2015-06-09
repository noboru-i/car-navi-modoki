#!/usr/bin/env bash

gem install --no-document checkstyle_filter-git saddler saddler-reporter-github

./gradlew check

cat app/build/reports/checkstyle/checkstyle.xml \
    | checkstyle_filter-git diff origin/master \
    | saddler report --require saddler/reporter/github --reporter Saddler::Reporter::Github::PullRequestReviewComment
