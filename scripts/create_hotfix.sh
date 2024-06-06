#!/bin/bash

message() {
  echo -e "\n######################################################################"
  echo "# $1"
  echo "######################################################################"
}

get_hotfix_version() {
  # 1. Get the latest tag, or fallback to a default value if no tags exist
  LATEST_TAG=$(git describe --tags --abbrev=0 2>/dev/null)
  if [[ -z "$LATEST_TAG" ]]; then
    # No tags found, use a default value
    LATEST_TAG="0.0.0"
  fi

  # 2. Parse the version using bash string manipulation
  V_MAJOR=$(echo "$LATEST_TAG" | cut -d'.' -f1)
  V_MINOR=$(echo "$LATEST_TAG" | cut -d'.' -f2)
  V_PATCH=$(echo "$LATEST_TAG" | cut -d'.' -f3)
  V_PATCH=$((V_PATCH + 1))
  NEXT_VERSION="$V_MAJOR.$V_MINOR.$V_PATCH"
  echo "NEXT_VERSION=$NEXT_VERSION"
}

message ">>> Check status"

[[ ! -x "$(command -v gh)" ]] && echo "gh not found, you need to install github CLI" && exit 1

gh auth status

# 1. Make sure branch is set to main
[[ $(git rev-parse --abbrev-ref HEAD) != "main" ]] && echo "ERROR: Checkout to main" && exit 1

# 2. Make sure branch is clean
[[ $(git status --porcelain) ]] && echo "ERROR: The branch is not clean, commit your changes before creating the release" && exit 1

message ">>> Pulling main"
git pull origin main
message ">>> Pulling tags"
git fetch --prune --prune-tags origin

get_hotfix_version

message ">>> Hotfix: $NEXT_VERSION"

# create branch
BRANCH_NAME="hotfix/$NEXT_VERSION"
read -r -p "Last tag version was '$LATEST_TAG', do you want to create '$BRANCH_NAME' branch ? [Y/n]:  " RESPONSE
if [[ $RESPONSE =~ ^([yY][eE][sS]|[yY])$ ]]; then
  echo "Creating branch '$BRANCH_NAME' from main..."

  git branch -D "$BRANCH_NAME"
  git checkout -b "$BRANCH_NAME" main
  git push origin "$BRANCH_NAME"

  # create pr if current branch is develop
  # gh pr create --base main --head "$BRANCH_NAME" --title "Feature - $NEXT_VERSION" --fill
else
  echo "Action cancelled exiting..."
  exit 1
fi


