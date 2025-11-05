export interface User {
    userId: number;
    email: string;
    firstName: string;
    lastName: string;
    mobileNumber?: string;
    gender?: string;
    country?: string;
    language?: string;
    role: string;
    suspended: boolean;
    createdAt: string;
    applicationCount: number;
}
