export class User  {
    constructor(
        public id: number,
        public username: string,
        public password: string,
        public enabled: boolean,
        public roles: string[],
    ) {}
}