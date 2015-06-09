#!/usr/bin/env bash

NOTIFIER_CONTEXT="saddler/android"

gem install --no-document checkstyle_filter-git saddler saddler-reporter-github github_status_notifier

github-status-notifier notify --state pending --context "${NOTIFIER_CONTEXT}"

./gradlew check

cat app/build/reports/checkstyle/checkstyle.xml | checkstyle_filter-git diff origin/master | saddler report --require saddler/reporter/github --reporter Saddler::Reporter::Github::PullRequestReviewComment
github-status-notifier notify --exit-status $? --context "${NOTIFIER_CONTEXT}"
