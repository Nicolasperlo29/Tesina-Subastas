import { Pipe, PipeTransform } from '@angular/core';
import { User } from './user';

@Pipe({
  name: 'filterByEmail',
  standalone: true
})
export class FilterByEmailPipe implements PipeTransform {

  transform(users: User[], email: string): User[] {
    if (!email) return users;
    const lowerEmail = email.toLowerCase();
    return users.filter(user =>
      user.email.toLowerCase().includes(lowerEmail)
    );
  }
}
