export default {
    // Common
    common: {
        confirm: 'Confirm',
        cancel: 'Cancel',
        save: 'Save',
        delete: 'Delete',
        edit: 'Edit',
        add: 'Add',
        search: 'Search',
        loading: 'Loading...',
        success: 'Operation successful',
        error: 'Operation failed',
        warning: 'Warning',
        info: 'Info',
        yes: 'Yes',
        no: 'No',
        back: 'Back',
        next: 'Next',
        previous: 'Previous',
        submit: 'Submit',
        reset: 'Reset',
        refresh: 'Refresh',
        close: 'Close',
        open: 'Open',
        view: 'View',
        download: 'Download',
        upload: 'Upload',
        export: 'Export',
        import: 'Import',
        confirmDelete: 'Confirm Delete',
        actions: 'Actions',
        status: 'Status',
        enabled: 'Enabled',
        disabled: 'Disabled',
        description: 'Description',
        sortOrder: 'Sort Order',
        showing: 'Showing',
        of: 'of',
        itemsPerPage: 'Items per page',
        noData: 'No data available',
        noSearchResults: 'No matching results found'
    },

    // Navigation
    nav: {
        dashboard: 'Dashboard',
        servers: 'Server Management',
        serverCategory: 'Server Categories',
        serverGroup: 'Server Groups',
        monitoring: 'Monitoring Center',
        settings: 'System Settings',
        profile: 'Profile',
        logout: 'Logout'
    },

    // Server Category
    serverCategory: {
        title: 'Server Category Management',
        list: 'Category List',
        category: 'Category',
        name: 'Category Name',
        confirmDelete: 'Are you sure you want to delete category "{name}"?'
    },
    
    // Server Management
    servers: {
        title: 'Server Management',
        category: 'Server Category',
        group: 'Server Group',
        list: 'Server List',
        add: 'Add Server',
        edit: 'Edit Server',
        delete: 'Delete Server',
        status: 'Status',
        online: 'Online',
        offline: 'Offline',
        location: 'Location',
        provider: 'Provider',
        specs: 'Specifications',
        actions: 'Actions'
    },

    // Group Management
    groups: {
        title: 'Group Management',
        subtitle: 'Manage your VPS groups by region and purpose',
        newGroup: 'New Group',
        editGroup: 'Edit Group',
        deleteGroup: 'Delete Group',
        groupDetails: 'Group Details',
        noDescription: 'No description',
        uncategorized: 'Uncategorized',
        selectCategory: 'Select Category',
        groupName: 'Group Name',
        groupDescription: 'Group Description',
        region: 'Region',
        country: 'Country',
        city: 'City',
        sortOrder: 'Sort Order',
        sortOrderHint: 'Lower numbers appear first',
        createGroup: 'Create Group',
        saveGroup: 'Save',
        cancelGroup: 'Cancel',
        confirmDeleteTitle: 'Confirm Delete',
        confirmDeleteSubtitle: 'This action cannot be undone, please proceed with caution',
        deleteWarning: 'This action cannot be undone, please proceed with caution!',
        groupToDelete: 'Group to be deleted:',
        confirmDeleteAction: 'Confirm Delete',
        groupCreatedSuccess: 'Group created successfully',
        groupUpdatedSuccess: 'Group updated successfully',
        groupDeletedSuccess: 'Group deleted successfully',
        groupCreateFailed: 'Failed to create group',
        groupUpdateFailed: 'Failed to update group',
        groupDeleteFailed: 'Failed to delete group',
        getCategoriesFailed: 'Failed to get category options',
        getGroupsFailed: 'Failed to get group options',
        getServerGroupsFailed: 'Failed to get server groups',
        close: 'Close'
    },

    // Category Management
    categories: {
        categoryName: 'Category Name',
        description: 'Description',
        multiLanguageSettings: 'Multi-language Settings',
        chineseName: '中文名称',
        englishName: 'English Name',
        chineseDescription: '中文描述',
        englishDescription: 'English Description',
        enableStatus: 'Enable Status',
        sort: 'Sort',
        statistics: 'Statistics',
        groupsServers: 'Groups / Servers',
        debugInfo: 'Debug Info',
        currentLanguage: 'Current Language',
        apiLanguageHeader: 'API Language Header',
        dataLoadCount: 'Data Load Count',
        hideDebugInfo: 'Hide Debug Info',
        totalCategories: 'Total {count} categories',
        multiLanguageSupport: 'Multi-language Support',
        multiLanguageInfo: 'Current language: {language}, data will automatically display corresponding names and descriptions based on your selected language.',
        chinese: 'Chinese',
        english: 'English',
        confirmDeleteCategory: 'Confirm delete category "{name}"?',
        deleteCategoryFailed: 'Failed to delete category',
        loadCategoriesFailed: 'Failed to load categories',
        saveCategoryFailed: 'Failed to save category'
    },
    
    // Dashboard
    dashboard: {
        welcome: 'Welcome back, {username}!',
        subtitle: 'This is your personal dashboard',
        onlineStatus: 'Online Status',
        userInfo: 'User Information',
        username: 'Username',
        email: 'Email',
        quickNavigation: 'Quick Navigation',


        profileDescription: 'Personal profile and account settings',
        tokenStatus: 'Token Status',
        tokenValid: 'Valid (Never Expires)',
        systemFeatures: 'System Features',
        jwtAuth: 'JWT Authentication Enabled',
        tokenNeverExpires: 'Token Never Expires',
        h2Database: 'H2 Database Storage',
        passwordEncryption: 'Secure Password Encryption',
        enabled: 'Enabled'
    },



    // Form Validation
    validation: {
        required: 'This field is required',
        email: 'Please enter a valid email address',
        minLength: 'Minimum {min} characters required',
        maxLength: 'Maximum {max} characters allowed',
        numeric: 'Please enter a number',
        phone: 'Please enter a valid phone number',
        url: 'Please enter a valid URL',
        ip: 'Please enter a valid IP address',
        invalidIP: 'Please enter a valid IP address format',
        port: 'Port number should be between 1-65535'
    },

    // System Settings
    settings: {
        title: 'System Settings',
        general: 'General Settings',
        theme: 'Theme Settings',
        language: 'Language Settings',
        notification: 'Notification Settings',
        security: 'Security Settings',
        about: 'About System'
    },

    // Theme Settings
    theme: {
        title: 'Theme Settings',
        light: 'Light Theme',
        dark: 'Dark Theme',
        auto: 'Follow System',
        primaryColor: 'Primary Color',
        secondaryColor: 'Secondary Color',
        accentColor: 'Accent Color',
        customTheme: 'Custom Theme'
    },
    
    // Authentication
    auth: {
        // Login
        login: {
            title: 'Welcome Back',
            subtitle: 'Please sign in to your account',
            username: 'Username',
            password: 'Password',
            loginButton: 'Sign In',
            loginButtonLoading: 'Signing In...',
            noAccount: "Don't have an account?",
            registerLink: 'Sign Up',
            forgotPassword: 'Forgot Password?'
        },
        
        // Register
        register: {
            title: 'Join Us',
            subtitle: 'Create your new account',
            username: 'Username',
            email: 'Email',
            password: 'Password',
            confirmPassword: 'Confirm Password',
            registerButton: 'Sign Up',
            registerButtonLoading: 'Signing Up...',
            hasAccount: 'Already have an account?',
            loginLink: 'Sign In',
            passwordStrength: 'Password Strength',
            passwordWeak: 'Weak',
            passwordMedium: 'Medium',
            passwordStrong: 'Strong',
            agreeTerms: 'I agree to the',
            termsOfService: 'Terms of Service',
            privacyPolicy: 'Privacy Policy'
        },
        
        // Error Messages
        errors: {
            emptyFields: 'Username and password cannot be empty',
            allFieldsRequired: 'All fields are required',
            pleaseFixErrors: 'Please fix the errors in the form before submitting',
            passwordMismatch: 'Passwords do not match',
            invalidCredentials: 'Invalid username or password',
            userExists: 'User already exists',
            networkError: 'Network connection error',
            serverError: 'Server error, please try again later'
        }
    },
    
    // Profile
    profile: {
        title: 'Profile',
        username: 'Username',
        email: 'Email',
        memberSince: 'Member since',
        changePassword: 'Change Password',
        currentPassword: 'Current Password',
        newPassword: 'New Password',
        confirmNewPassword: 'Confirm New Password',
        saveChanges: 'Save Changes',
        reset: 'Reset',
        updateSuccess: 'Profile updated successfully',
        updateError: 'Failed to update profile',
        usernameFormat: 'Username can only contain letters, numbers and underscores'
    },
    
    // Forgot Password
    forgotPassword: {
        title: 'Forgot Password',
        subtitle: 'Enter your email address and we will send you a verification code',
        email: 'Email Address',
        sendCode: 'Send Code',
        sendingCode: 'Sending...',
        verificationCode: 'Verification Code',
        newPassword: 'New Password',
        confirmPassword: 'Confirm Password',
        resetPassword: 'Reset Password',
        resetting: 'Resetting...',
        backToEmail: 'Back',
        backToLogin: 'Back to Login',
        rememberPassword: 'Remember your password?',
        codeSent: 'Verification code has been sent to your email',
        codeResent: 'Verification code has been resent',
        codeInfo: 'Verification code has been sent to {email}, please check your email',
        codeFormat: 'Verification code must be 6 digits',
        resendCode: 'Resend Code',
        resendCountdown: 'Resend in {seconds}s',
        resetSuccess: 'Password reset successfully, redirecting to login page',
        sendCodeError: 'Failed to send verification code',
        resetError: 'Failed to reset password'
    },
    
    // Footer
    footer: {
        company: 'VPS Management System Ltd.',
        privacy: 'Privacy Policy',
        terms: 'Terms of Service',
        support: 'Technical Support',
        poweredBy: 'Powered by Vue.js + Spring Boot'
    }
}