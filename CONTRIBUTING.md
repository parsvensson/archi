# Contributing & Workflow Guide

This repository is a personal fork of [archimatetool/archi](https://github.com/archimatetool/archi) maintained by @parsvensson. It contains custom features merged into an integration branch for personal builds.

## Repository Structure

### Remotes

| Remote   | URL                                        | Purpose                    |
|----------|--------------------------------------------|----------------------------|
| `origin` | https://github.com/archimatetool/archi.git | Upstream official repo     |
| `myfork` | git@github.com:parsvensson/archi.git       | Personal fork for features |

### Branch Strategy

```
origin/master (upstream)
       │
       ▼
    master ─────────────────────────► Tracks upstream, kept in sync
       │
       ├──► feature/* ──┐
       │                │
       │                ├──► my-archi ──► Integration branch for releases
       │                │
       └──► feature/* ──┘
```

| Branch      | Purpose                                              |
|-------------|------------------------------------------------------|
| `master`    | Mirrors upstream `origin/master`. Never commit here. |
| `my-archi`  | Integration branch. All features merged here.        |
| `feature/*` | Individual feature branches, based off `master`.     |

## Current Features

| Branch                              | Description                                    | Status   |
|-------------------------------------|------------------------------------------------|----------|
| `feature/html-report-postprocess`   | Post-process command support for HTML reports  | Merged   |
| `feature/themed-palette-tree-icons` | Themed icons for palette and model tree        | Merged   |

## Common Workflows

### 1. Create a New Feature

```bash
# Ensure master is up-to-date with upstream
git checkout master
git fetch origin
git merge origin/master

# Create feature branch
git checkout -b feature/my-new-feature

# Develop your feature...
# Commit changes...

# Push to your fork
git push -u myfork feature/my-new-feature
```

### 2. Sync Upstream Changes into master

```bash
git checkout master
git fetch origin
git merge origin/master
git push myfork master
```

### 3. Sync Upstream Changes into my-archi

After syncing master (see above):

```bash
git checkout my-archi
git merge master

# Resolve any conflicts between upstream changes and your features
# Test the build

git push myfork my-archi
```

### 4. Merge a Feature into my-archi

```bash
git checkout my-archi
git merge feature/my-feature-name

# Or to preserve feature history with a merge commit:
git merge --no-ff feature/my-feature-name

git push myfork my-archi
```

### 5. Rebase a Feature onto Latest Upstream

Use this to update a feature branch with upstream changes before merging:

```bash
# First sync master
git checkout master
git fetch origin
git merge origin/master

# Rebase feature
git checkout feature/my-feature
git rebase master

# Force-push the rebased feature (only if not yet merged!)
git push myfork feature/my-feature --force-with-lease
```

## Building

This is a Maven/Tycho project for Eclipse RCP.

```bash
# Run the build
mvn clean verify

# Run tests
mvn test
```

For full build documentation, see the upstream wiki:
https://github.com/archimatetool/archi/wiki/Developer-Documentation

## Release Process

1. Ensure `my-archi` has all desired features merged
2. Sync latest upstream changes into `my-archi`
3. Test the build locally: `mvn clean verify`
4. Tag if desired: `git tag -a v5.x.x-custom -m "Release description"`
5. Push: `git push myfork my-archi --tags`

## Important Notes

- **Never commit directly to `master`** - it should only receive merges from upstream
- **Always branch features from `master`** - not from `my-archi`
- **Keep features independent** - each feature branch should work standalone
- **Test before merging** - build and test features before merging into `my-archi`

## Stashed Work

There may be work-in-progress in git stash. Check with:

```bash
git stash list
git stash show -p stash@{0}  # View contents
```

Current stash: "WIP: JustJ JRE bundling attempt"

## For AI Agents

When working on this repository:

1. **Check current branch** before making changes: `git branch --show-current`
2. **New features** should branch from `master`, not `my-archi`
3. **To build a release**, work on the `my-archi` branch
4. **Upstream sync** should be done on `master` first, then merged into `my-archi`
5. **Push to `myfork`**, not `origin` (which is the upstream repo)
