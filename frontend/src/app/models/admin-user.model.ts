export interface User {
    showDropdown: any;
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
