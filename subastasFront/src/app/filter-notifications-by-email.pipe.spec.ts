import { FilterNotificationsByEmailPipe } from './filter-notifications-by-email.pipe';

describe('FilterNotificationsByEmailPipe', () => {
  it('create an instance', () => {
    const pipe = new FilterNotificationsByEmailPipe();
    expect(pipe).toBeTruthy();
  });
});
