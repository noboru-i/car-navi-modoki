#!/usr/bin/env bash

./gradlew check

gem install --no-document checkstyle_filter-git saddler saddler-reporter-github github_status_notifier
cat app/build/reports/checkstyle/checkstyle.xml | checkstyle_filter-git diff origin/master | saddler report --require saddler/reporter/github --reporter Saddler::Reporter::Github::PullRequestReviewComment
github-status-notifier notify --exit-status $? --context "saddler/android"
