# 🔐 Secrets Management Guide

This guide explains how to properly handle sensitive information in the EduAI Tutor project.

## ⚠️ Important: Never Commit Secrets!

**NEVER commit:**

- API keys
- Database credentials
- Private keys
- Passwords
- Authentication tokens
- Signing keys

## 🚀 Quick Start

### 1. Create `.env` File (Git-Ignored)

Create `local.properties` in the project root:

```properties
# local.properties (DO NOT COMMIT)
# This file is in .gitignore and will not be committed

# API Configuration (if needed in future)
api.key=your_key_here
api.secret=your_secret_here

# Signing Configuration
signing.key.path=/path/to/keystore.jks
signing.key.password=your_password
signing.key.alias=your_alias
```

### 2. Environment Variables

Set environment variables instead of committing secrets:

```bash
# macOS/Linux
export API_KEY="your_key_here"
export API_SECRET="your_secret_here"

# Windows PowerShell
$env:API_KEY="your_key_here"
$env:API_SECRET="your_secret_here"
```

### 3. Access in Build.gradle

In `app/build.gradle.kts`:

```kotlin
android {
    compileSdk = 35
    
    defaultConfig {
        // Read from environment or local.properties
        val apiKey = System.getenv("API_KEY") ?: "default_value"
        
        buildConfigField("String", "API_KEY", "\"$apiKey\"")
    }
}
```

## 📋 Files in .gitignore

The following files are intentionally ignored:

```
# Secrets
local.properties
.env
.env.local
*.key
*.pem
secrets.json
keystore.jks
*.keystore

# Build artifacts containing potential secrets
app/build/
build/

# IDE with local settings
.idea/
.gradle/

# Generated files
generated/
```

## 🔑 API Keys Management

### Current Status: No External APIs

✅ **EduAI Tutor uses 100% on-device inference**

- No Gemini API keys
- No external API keys
- No cloud services
- All processing local

### If Future APIs Are Added

1. **Never hardcode API keys**
2. **Use environment variables**
3. **Store in secure backend** (if applicable)
4. **Rotate compromised keys immediately**
5. **Use key management services** (AWS KMS, Google Cloud KMS, etc.)

## 🛡️ Pre-Commit Hooks

Install git pre-commit hook to prevent accidental commits:

```bash
# Create .git/hooks/pre-commit
#!/bin/bash

# Check for secrets in staged files
if git diff --cached | grep -iE "(AIza|api_key|password|secret|token)" | grep -v ".gitignore"; then
    echo "❌ ERROR: Potential secret detected in staged files!"
    echo "Please remove secrets before committing"
    exit 1
fi

exit 0
```

Make it executable:

```bash
chmod +x .git/hooks/pre-commit
```

## 🔍 Scanning for Secrets

### Using git-secrets

Install:

```bash
brew install git-secrets
```

Configure:

```bash
git secrets --install
git secrets --register-aws
git secrets --register-github
```

Scan repository:

```bash
git secrets --scan
```

### Using TruffleHog

```bash
pip install truffleHog

trufflehog filesystem . --json
```

## 🚨 If You Accidentally Committed a Secret

### Immediate Actions

1. **Revoke the secret immediately**
    - If API key: Regenerate/revoke in service dashboard
    - If password: Change password
    - If token: Invalidate token

2. **Remove from Git history**

```bash
# Remove file from all history (dangerous!)
git filter-branch --tree-filter 'rm -f local.properties' HEAD

# Push to remote
git push origin master --force
```

⚠️ **Warning**: This rewrites history and affects all collaborators!

Better approach using `git filter-repo`:

```bash
# Install
pip install git-filter-repo

# Remove file from history
git filter-repo --path local.properties --invert-paths

# Push
git push origin master --force
```

### Notify the Team

1. Inform all developers about the leak
2. Ask them to pull the latest changes
3. Document in commit message what was leaked and when

## 📊 Current Project Status

### ✅ No Secrets Detected

This project currently has:

- ✅ No API keys in code
- ✅ No hardcoded passwords
- ✅ No authentication tokens
- ✅ No private credentials
- ✅ Only on-device processing

### 🏗️ Infrastructure

The project uses:

- **Local Database**: Room (on-device, unencrypted)
- **Settings Storage**: DataStore (on-device)
- **AI Processing**: On-device models (no cloud)

## 🔒 Best Practices

### 1. Use .gitignore

```gitignore
local.properties
.env
*.key
*.keystore
secrets.json
```

### 2. Use Environment Variables

```kotlin
val apiKey = System.getenv("API_KEY") ?: throw Exception("API_KEY not set")
```

### 3. Use Secrets Management Tools

- **GitHub Secrets** (for CI/CD)
- **AWS Secrets Manager**
- **Google Cloud Secret Manager**
- **HashiCorp Vault**

### 4. Code Review

- Always review for secrets in PRs
- Mention in PR template: "No secrets included?"

### 5. Monitoring

- Enable GitHub secret scanning
- Use dependency checking tools
- Regular security audits

## 📚 Resources

- [GitHub Secret Scanning](https://docs.github.com/en/code-security/secret-scanning)
- [git-secrets](https://github.com/awslabs/git-secrets)
- [TruffleHog](https://github.com/trufflesecurity/trufflehog)
- [OWASP Secrets Management](https://owasp.org/www-community/Secrets_Management)

## ✅ Checklist

Before committing:

- [ ] No API keys in code
- [ ] No passwords in code
- [ ] No private keys in code
- [ ] No authentication tokens
- [ ] All sensitive files in `.gitignore`
- [ ] Ran `git secrets --scan`
- [ ] All environment variables documented

## 💬 Questions?

For questions about secrets management:

1. Check this document first
2. Review `.gitignore` file
3. Check team security guidelines
4. Ask team lead/security officer

---

**Last Updated**: 2024
**Status**: ✅ No Secrets in Repository