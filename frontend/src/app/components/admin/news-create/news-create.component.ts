import {Component, OnInit} from '@angular/core';
import {NewsData} from "../../../dtos/news-data";
import {NewsService} from "../../../services/news.service";
import {ActivatedRoute, Router} from "@angular/router";
import {FormsModule, NgForm} from "@angular/forms";
import {Observable} from "rxjs";
import {formatDate, NgClass} from "@angular/common";
import {ToastrService} from "ngx-toastr";
import {CommonModule} from "@angular/common";

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

  news: NewsData = {
    title: '',
    summary: '',
    content: '',
    imageUrl: [],
    dateOfNews: new Date()
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
    console.log("submitted");
    if (form.valid) {
      const newsFormData = this.createFormData(this.news);
      let observable: Observable<NewsData>;
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
          this.toastr.error(`<ul>${errorList}</ul>`, 'Error creating news', {
            enableHtml: true,
          });
        },
      });
    }
  }

  /*private methods for validation*/

  private createFormData(news: NewsData): FormData {
    const formData = new FormData();
    formData.append('title', news.title);
    formData.append('summary', news.summary);
    formData.append('content', news.content);
    formData.append('dateOfNews', news.dateOfNews.toString()); // Ensure correct date format

    if (this.selectedFiles) {
      for (const file of this.selectedFiles) {
        formData.append('images', file, file.name); // Correct key for file
      }
    }
    return formData;
  }


  private validateNews(news: NewsData) {
    console.log("validation start");
    let errorMessage = '';

    if (this.empty(news.title) || (this.isEmpty(news.title))) {
      errorMessage += 'The title cannot be blank\n';
    }

    if (this.toLongText(news.title, 255)) {
      errorMessage += 'The title cannot exceed the limit of 255 characters\n';
    }

    if (this.empty(news.summary) || (this.isEmpty(news.summary))) {
      errorMessage += 'The summary cannot be blank\n';
    }

    if (this.toLongText(news.summary, 1024)) {
      errorMessage += 'The summary cannot exceed the limit of 1024 characters\n';
    }

    if (this.empty(news.content) || (this.isEmpty(news.content))) {
      errorMessage += 'The content cannot be blank\n';
    }

    if (this.toLongText(news.content, 4096)) {
      errorMessage += 'The content cannot exceed the limit of 4096 characters\n';
    }

    if (formatDate(news.dateOfNews, 'yyyy-MM-dd', 'en_US') === formatDate(new Date(0), 'yyyy-MM-dd', 'en_US')) {
      errorMessage += 'No date of news f was given\n';
    }
    if (news.dateOfNews !== undefined &&
      formatDate(this.news.dateOfNews, 'yyyy-MM-dd', 'en_US') > formatDate(new Date(), 'yyyy-MM-dd', 'en_US')) {
      errorMessage += 'The given date of news lies in the future\n';
    }
    return this.empty(errorMessage);
  }

  empty(str: string | undefined) {
    return !str || !/[^\s]+/.test(str);
  }

  isEmpty(data: object | string) {
    if (typeof (data) === 'object') {
      if (JSON.stringify(data) === '{}' || JSON.stringify(data) === '[]') {
        return true;
      } else if (!data) {
        return true;
      }
      return false;
    } else if (typeof (data) === 'string') {
      if (!data.trim()) {
        return true;
      }
      return false;
    } else {
      return false;
    }
  }

  private toLongText(name: string | undefined, length: number) {
    if (name !== undefined && (name.length > length || name.split(' ').length > length)) {
      return true;
    } else {
      return false;
    }
  }


  readImage(event: Event): void {
    const imageInput = event.target as HTMLInputElement;
    this.selectedFiles = [];
    this.news.imageUrl = []; // Reset the image URL array

    if (imageInput.files && imageInput.files.length > 0) {
      for (let i = 0; i < imageInput.files.length; i++) {
        const file = imageInput.files.item(i);
        if (file) {
          this.selectedFiles.push(file);

          const reader = new FileReader();
          reader.onload = (e: ProgressEvent<FileReader>) => {
            const result = e.target?.result as string;
            if (result) {
              this.news.imageUrl.push(result);
              this.displayImages(this.news.imageUrl);
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
      container.innerHTML = ''; // Clear the container before displaying new images

      images.forEach((image) => {
        const imgElement = document.createElement('img');
        imgElement.setAttribute('src', image);
        imgElement.setAttribute('alt', 'Preview Image');
        imgElement.style.maxHeight = '100px';
        imgElement.style.maxWidth = '100px';
        imgElement.style.margin = '5px'; // Add some spacing between images
        container.appendChild(imgElement);
      });
    }
  }

}

