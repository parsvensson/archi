# GitHub Copilot Instructions

Read `CONTRIBUTING.md` in the repository root for the full workflow documentation.

## Repository Overview

This is a fork of archimatetool/archi with custom features.

## Quick Reference

- **Upstream remote:** `origin` (archimatetool/archi)
- **Fork remote:** `myfork` (parsvensson/archi)
- **Integration branch:** `my-archi` (build releases from here)
- **Feature branches:** `feature/*` (always branch from `master`)

## Key Rules

1. New features must branch from `master`, not `my-archi`
2. Push to `myfork`, never directly to `origin`
3. To sync upstream: update `master` first, then merge into `my-archi`
4. Build with Maven: `mvn clean verify`
