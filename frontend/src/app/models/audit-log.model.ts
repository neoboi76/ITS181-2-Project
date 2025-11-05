export interface AuditLog {
    auditId: number;
    user?: {
        userId: number;
        email: string;
        firstName: string;
        lastName: string;
    };
    action: string;
    ipAddress: string;
    userAgent?: string;
    details?: string;
    timestamp: string;
    success: boolean;
    errorMessage?: string;
}