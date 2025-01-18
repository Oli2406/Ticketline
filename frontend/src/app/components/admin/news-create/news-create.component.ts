import {Component, OnInit} from '@angular/core';
import {NewsDto} from "../../../dtos/news-data";
import {NewsService} from "../../../services/news.service";
import {ActivatedRoute, Router} from "@angular/router";
import {FormsModule, NgForm} from "@angular/forms";
import {Observable} from "rxjs";
import {CommonModule, NgClass} from "@angular/common";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-news-create',
  standalone: true,
  imports: [
    FormsModule,
    NgClass,
    CommonModule
  ],
  templateUrl: './news-create.component.html',
  styleUrl: './news-create.component.scss'
})
export class NewsCreateComponent implements OnInit {

  news: NewsDto = {
    title: '',
    summary: '',
    content: '',
    images: [],
    date: new Date()
  };
  private selectedFiles: File[];

  constructor(private service: NewsService,
              private route: ActivatedRoute,
              private toastr: ToastrService,
              private router: Router) {
  }

  ngOnInit(): void {
    this.route.data.subscribe();
  }

  public onSubmit(form: NgForm): void {
    if (form.valid) {

      const newsFormData = this.createFormData(this.news);
      let observable: Observable<NewsDto>;
      observable = this.service.createNews(newsFormData);
      observable.subscribe({
        next: () => {
          this.toastr.success('News successfully submitted');
          this.router.navigate(['']);
        }, error: (err) => {
          console.error('Error during news creation:', err.message);
          const errors = Array.isArray(err.message)
            ? err.message
            : err.message.split(/\n/);
          const errorList = errors
          .map((error) => `<li>${error.trim()}</li>`)
          .join('');
          this.toastr.error(`<ul>${errorList}</ul>`, 'Error creating news.', {
            enableHtml: true,
          });
        },
      });
    }
  }

  private createFormData(news: NewsDto): FormData {
    const formData = new FormData();
    formData.append('title', news.title);
    formData.append('summary', news.summary);
    formData.append('content', news.content);
    formData.append('date', news.date.toISOString().split('T')[0]);

    if (this.selectedFiles) {
      for (const file of this.selectedFiles) {
        formData.append('images', file, file.name);
      }
    }
    return formData;
  }

  readImage(event: Event): void {
    const imageInput = event.target as HTMLInputElement;
    this.selectedFiles = [];
    this.news.images = []; // Reset the image URL array

    if (imageInput.files && imageInput.files.length > 0) {
      for (let i = 0; i < imageInput.files.length; i++) {
        const file = imageInput.files.item(i);
        let fileSize = file.size;
        let fileSizeInMb = fileSize / (1024 ** 2);
        if (fileSizeInMb > 10) {
          this.toastr.error('Image Upload File is too big (larger than 10MB) please change your images or news creation will fail' + '\n ' + file.name)
        }

        if (file) {
          this.selectedFiles.push(file);

          const reader = new FileReader();
          reader.onload = (e: ProgressEvent<FileReader>) => {
            const result = e.target?.result as string;
            if (result) {
              this.news.images.push(result);
              this.displayImages(this.news.images);
            }
          };
          reader.readAsDataURL(file);
        }
      }
    }
  }


  displayImages(images: string[]): void {
    const container = document.getElementById('imageContainer');
    if (container) {
      container.innerHTML = '';

      images.forEach((image) => {
        const imgElement = document.createElement('img');
        imgElement.setAttribute('src', image);
        imgElement.setAttribute('alt', 'Preview Image');
        imgElement.style.maxHeight = '100px';
        imgElement.style.maxWidth = '100px';
        imgElement.style.margin = '5px';
        container.appendChild(imgElement);
      });
    }
  }

  protected readonly Date = Date;
}

