class Task {
    constructor(
        public id: string,
        public name: string,
        public status: string,
        public startTime: Date,
        public endTime: Date | null = null,
    ) {}
        
}