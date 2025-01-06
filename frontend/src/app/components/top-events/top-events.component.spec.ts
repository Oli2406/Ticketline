import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TopEventsComponent } from './top-events.component';

describe('TopEventsComponent', () => {
  let component: TopEventsComponent;
  let fixture: ComponentFixture<TopEventsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TopEventsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TopEventsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
